package com.cgovern.cgovern.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.cgovern.cgovern.domain.AppService;
import com.cgovern.cgovern.domain.ExecutionMode;
import com.cgovern.cgovern.domain.Job;
import com.cgovern.cgovern.domain.JobRun;
import com.cgovern.cgovern.domain.NotificationItem;
import com.cgovern.cgovern.domain.RemoteServer;
import com.cgovern.cgovern.domain.RunStatus;
import com.cgovern.cgovern.domain.Task;
import com.cgovern.cgovern.repo.JobRunRepository;
import com.cgovern.cgovern.repo.NotificationRepository;

@Service
public class JobExecutionService {

    private final CatalogService catalog;
    private final JobRunRepository runs;
    private final NotificationRepository notifications;
    private final RestClient restClient = RestClient.create();

    public JobExecutionService(CatalogService catalog, JobRunRepository runs, NotificationRepository notifications) {
        this.catalog = catalog;
        this.runs = runs;
        this.notifications = notifications;
    }

    @Transactional
    public List<JobRun> runServicePipeline(Long serviceId) {
        AppService service = catalog.service(serviceId);
        List<Job> orderedJobs = catalog.jobs(serviceId);
        List<JobRun> results = new ArrayList<>();
        for (Job job : orderedJobs) {
            JobRun run = executeJob(job);
            results.add(run);
            if (run.getStatus() == RunStatus.FAILED) {
                notify("Pipeline stopped", service.getName() + " failed at sequential job " + job.getName());
                break;
            }
        }
        return results;
    }

    @Transactional
    public JobRun executeJob(Job job) {
        JobRun run = new JobRun();
        run.setJob(job);
        run.setStartedAt(Instant.now());
        run.setStatus(RunStatus.RUNNING);
        StringBuilder log = new StringBuilder();
        log.append("Job ").append(job.getName()).append(" started (jobs always run sequentially).\n");

        List<Task> tasks = catalog.tasks(job.getId());
        Map<Integer, List<Task>> groups = new LinkedHashMap<>();
        tasks.stream()
                .sorted(Comparator.comparingInt(Task::getParallelGroup).thenComparingInt(Task::getOrderIndex))
                .forEach(task -> groups.computeIfAbsent(task.getParallelGroup(), key -> new ArrayList<>()).add(task));

        boolean failed = false;
        for (Map.Entry<Integer, List<Task>> entry : groups.entrySet()) {
            List<Task> group = entry.getValue();
            boolean parallel = group.stream().anyMatch(task -> task.getExecutionMode() == ExecutionMode.PARALLEL)
                    && group.size() > 1;
            log.append("Group ").append(entry.getKey()).append(" mode=")
                    .append(parallel ? "PARALLEL" : "SEQUENTIAL").append("\n");
            if (parallel) {
                failed = !runParallel(job, group, log);
            } else {
                for (Task task : group) {
                    if (!dispatchTask(job, task, log)) {
                        failed = true;
                        break;
                    }
                }
            }
            if (failed) {
                break;
            }
        }

        run.setLogOutput(log.toString());
        run.setFinishedAt(Instant.now());
        run.setStatus(failed ? RunStatus.FAILED : RunStatus.SUCCEEDED);
        JobRun saved = runs.save(run);
        notify("Job " + run.getStatus(), job.getName() + " finished on "
                + (job.getRemoteServer() == null ? "unassigned agent" : job.getRemoteServer().getName()));
        return saved;
    }

    private boolean runParallel(Job job, List<Task> group, StringBuilder log) {
        try (ExecutorService pool = Executors.newFixedThreadPool(Math.min(4, group.size()))) {
            List<Callable<Boolean>> work = group.stream()
                    .map(task -> (Callable<Boolean>) () -> dispatchTask(job, task, log))
                    .toList();
            return pool.invokeAll(work).stream().allMatch(future -> {
                try {
                    return Boolean.TRUE.equals(future.get());
                } catch (Exception ex) {
                    return false;
                }
            });
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.append("Parallel group interrupted\n");
            return false;
        }
    }

    private synchronized boolean dispatchTask(Job job, Task task, StringBuilder log) {
        RemoteServer server = job.getRemoteServer();
        log.append("  Task ").append(task.getName()).append(" capability=").append(task.getCapability());
        if (server == null) {
            log.append(" -> no remote server assigned\n");
            return false;
        }
        if (!server.getAllowedCapabilities().toUpperCase().contains(task.getCapability().toUpperCase())) {
            log.append(" -> rejected: capability not allowlisted on ").append(server.getName()).append("\n");
            return false;
        }
        String body = "{\"taskId\":" + task.getId() + ",\"command\":\"" + sanitize(task.getCommand()) + "\"}";
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String signature = HmacSigner.sign(server.getSharedSecret(), timestamp, body);
        if (server.isSimulated()) {
            log.append(" -> simulated agent ").append(server.getName())
                    .append(" hmac=").append(signature, 0, 12).append("...\n");
            return true;
        }
        try {
            restClient.post()
                    .uri(server.getBaseUrl() + "/agent/execute")
                    .header("X-CGovern-Timestamp", timestamp)
                    .header("X-CGovern-Signature", signature)
                    .header("X-CGovern-Agent", String.valueOf(server.getId()))
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.append(" -> dispatched to ").append(server.getBaseUrl()).append("\n");
            return true;
        } catch (Exception ex) {
            log.append(" -> dispatch failed: ").append(ex.getMessage()).append("\n");
            return false;
        }
    }

    private void notify(String title, String body) {
        NotificationItem item = new NotificationItem();
        item.setTitle(title);
        item.setBody(body);
        item.setCreatedAt(Instant.now());
        notifications.save(item);
    }

    private String sanitize(String command) {
        if (command == null) {
            return "";
        }
        return command.replace("\"", "'");
    }
}
