package com.cgovern.cgovern.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cgovern.cgovern.domain.AppService;
import com.cgovern.cgovern.domain.FeatureModule;
import com.cgovern.cgovern.domain.Job;
import com.cgovern.cgovern.domain.Organization;
import com.cgovern.cgovern.domain.RemoteServer;
import com.cgovern.cgovern.domain.Task;
import com.cgovern.cgovern.repo.AppServiceRepository;
import com.cgovern.cgovern.repo.JobRepository;
import com.cgovern.cgovern.repo.OrganizationRepository;
import com.cgovern.cgovern.repo.RemoteServerRepository;
import com.cgovern.cgovern.repo.TaskRepository;

@Service
public class CatalogService {

    private final OrganizationRepository orgs;
    private final AppServiceRepository services;
    private final JobRepository jobs;
    private final TaskRepository tasks;
    private final RemoteServerRepository servers;

    public CatalogService(OrganizationRepository orgs, AppServiceRepository services, JobRepository jobs,
            TaskRepository tasks, RemoteServerRepository servers) {
        this.orgs = orgs;
        this.services = services;
        this.jobs = jobs;
        this.tasks = tasks;
        this.servers = servers;
    }

    public List<Organization> orgs(FeatureModule module) {
        return orgs.findByModuleOrderByNameAsc(module);
    }

    public Organization org(Long id) {
        return orgs.findById(id).orElseThrow();
    }

    @Transactional
    public Organization saveOrg(Organization organization) {
        return orgs.save(organization);
    }

    @Transactional
    public void deleteOrg(Long id) {
        Organization organization = org(id);
        services.findByOrganizationIdOrderByNameAsc(id).forEach(service -> deleteService(service.getId()));
        orgs.delete(organization);
    }

    public List<AppService> services(Long orgId) {
        return services.findByOrganizationIdOrderByNameAsc(orgId);
    }

    public AppService service(Long id) {
        return services.findById(id).orElseThrow();
    }

    @Transactional
    public AppService saveService(AppService service) {
        return services.save(service);
    }

    @Transactional
    public void deleteService(Long id) {
        AppService service = service(id);
        jobs.findByServiceIdOrderByOrderIndexAsc(id).forEach(job -> deleteJob(job.getId()));
        services.delete(service);
    }

    public List<Job> jobs(Long serviceId) {
        return jobs.findByServiceIdOrderByOrderIndexAsc(serviceId);
    }

    public Job job(Long id) {
        return jobs.findById(id).orElseThrow();
    }

    @Transactional
    public Job saveJob(Job job) {
        return jobs.save(job);
    }

    @Transactional
    public void deleteJob(Long id) {
        Job job = job(id);
        tasks.findByJobIdOrderByParallelGroupAscOrderIndexAsc(id).forEach(tasks::delete);
        jobs.delete(job);
    }

    public List<Task> tasks(Long jobId) {
        return tasks.findByJobIdOrderByParallelGroupAscOrderIndexAsc(jobId);
    }

    public Task task(Long id) {
        return tasks.findById(id).orElseThrow();
    }

    @Transactional
    public Task saveTask(Task task) {
        return tasks.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        tasks.deleteById(id);
    }

    public List<RemoteServer> servers() {
        return servers.findAll();
    }

    public RemoteServer server(Long id) {
        return servers.findById(id).orElseThrow();
    }

    @Transactional
    public RemoteServer saveServer(RemoteServer server) {
        return servers.save(server);
    }

    @Transactional
    public void deleteServer(Long id) {
        jobs.findByRemoteServerId(id).forEach(job -> {
            job.setRemoteServer(null);
            jobs.save(job);
        });
        servers.deleteById(id);
    }
}
