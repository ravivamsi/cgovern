package com.cgovern.cgovern.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cgovern.cgovern.domain.AppService;
import com.cgovern.cgovern.domain.ExecutionMode;
import com.cgovern.cgovern.domain.FeatureModule;
import com.cgovern.cgovern.domain.Job;
import com.cgovern.cgovern.domain.JobTemplate;
import com.cgovern.cgovern.domain.NotificationItem;
import com.cgovern.cgovern.domain.Organization;
import com.cgovern.cgovern.domain.Plugin;
import com.cgovern.cgovern.domain.RemoteServer;
import com.cgovern.cgovern.domain.Task;
import com.cgovern.cgovern.domain.UserAccount;
import com.cgovern.cgovern.repo.AppServiceRepository;
import com.cgovern.cgovern.repo.JobRepository;
import com.cgovern.cgovern.repo.JobTemplateRepository;
import com.cgovern.cgovern.repo.NotificationRepository;
import com.cgovern.cgovern.repo.OrganizationRepository;
import com.cgovern.cgovern.repo.PluginRepository;
import com.cgovern.cgovern.repo.RemoteServerRepository;
import com.cgovern.cgovern.repo.TaskRepository;
import com.cgovern.cgovern.repo.UserAccountRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserAccountRepository users, PasswordEncoder encoder, Environment env,
            OrganizationRepository orgs, AppServiceRepository services, JobRepository jobs, TaskRepository tasks,
            RemoteServerRepository servers, PluginRepository plugins, JobTemplateRepository templates,
            NotificationRepository notifications) {
        return args -> {
            if (users.count() == 0) {
                UserAccount admin = new UserAccount();
                admin.setFullName(env.getProperty("cgovern.demo-user.name", "CGovern Admin"));
                admin.setEmail(env.getProperty("cgovern.demo-user.email", "admin@cgovern.dev"));
                admin.setPasswordHash(encoder.encode(env.getProperty("cgovern.demo-user.password", "admin123")));
                admin.setRole("ROLE_ADMIN");
                users.save(admin);
            }
            if (servers.count() == 0) {
                servers.save(server("ci-worker-east", "us-east-1", "http://agent-east:8443", "BUILD,TEST"));
                servers.save(server("cd-worker-west", "us-west-2", "http://agent-west:8443", "DEPLOY,MONITOR"));
                servers.save(server("shared-runner", "eu-central-1", "http://agent-eu:8443", "BUILD,TEST,DEPLOY,MONITOR"));
            }
            if (orgs.count() == 0) {
                RemoteServer ciWorker = servers.findAll().get(0);
                Organization platform = org(FeatureModule.CI, "Platform Engineering", "Shared CI for platform services");
                orgs.save(platform);
                AppService payments = service(platform, "payments-api", "https://git.example/payments-api");
                services.save(payments);
                Job compile = job(payments, "compile", 1, ciWorker);
                Job test = job(payments, "verify", 2, ciWorker);
                jobs.save(compile);
                jobs.save(test);
                tasks.save(task(compile, "checkout", "git clone $REPO", 1, 1, ExecutionMode.SEQUENTIAL, "BUILD"));
                tasks.save(task(compile, "unit-tests", "./mvnw test", 1, 2, ExecutionMode.PARALLEL, "TEST"));
                tasks.save(task(compile, "lint", "./mvnw checkstyle:check", 1, 3, ExecutionMode.PARALLEL, "TEST"));
                tasks.save(task(test, "integration", "./mvnw verify", 1, 1, ExecutionMode.SEQUENTIAL, "TEST"));

                orgs.save(org(FeatureModule.CD, "Release Train", "Production promotion path"));
                orgs.save(org(FeatureModule.CT, "Quality Guild", "Nightly and contract tests"));
                orgs.save(org(FeatureModule.CM, "SRE", "SLO monitors and paging"));
                orgs.save(org(FeatureModule.AGENTIC, "Autonomy Lab", "Watchers that open incidents"));
            }
            if (plugins.count() == 0) {
                plugins.save(plugin("GitHub Checks", "Publish job status to pull requests"));
                plugins.save(plugin("Slack Alerts", "Route notifications to channels"));
                plugins.save(plugin("OpenTelemetry", "Export traces from remote agents"));
            }
            if (templates.count() == 0) {
                templates.save(template(FeatureModule.CI, "Maven verify", "checkout, test, package"));
                templates.save(template(FeatureModule.CD, "Blue/green deploy", "bake, smoke, shift traffic"));
                templates.save(template(FeatureModule.CT, "Contract suite", "provider + consumer tests in parallel"));
                templates.save(template(FeatureModule.CM, "SLO burn", "scrape metrics, evaluate, page"));
                templates.save(template(FeatureModule.AGENTIC, "Diagnose incident", "gather logs, hypothesize, propose fix"));
            }
            if (notifications.count() == 0) {
                NotificationItem item = new NotificationItem();
                item.setTitle("Control plane ready");
                item.setBody("Demo agents are simulated. Register a remote URL to dispatch HMAC-signed jobs.");
                notifications.save(item);
            }
        };
    }

    private RemoteServer server(String name, String region, String url, String caps) {
        RemoteServer server = new RemoteServer();
        server.setName(name);
        server.setRegion(region);
        server.setBaseUrl(url);
        server.setSharedSecret("demo-secret-" + name);
        server.setAllowedCapabilities(caps);
        server.setSimulated(true);
        server.setLastHeartbeat("seeded");
        return server;
    }

    private Organization org(FeatureModule module, String name, String description) {
        Organization organization = new Organization();
        organization.setModule(module);
        organization.setName(name);
        organization.setDescription(description);
        return organization;
    }

    private AppService service(Organization organization, String name, String repo) {
        AppService service = new AppService();
        service.setOrganization(organization);
        service.setName(name);
        service.setDescription("Seeded service");
        service.setRepositoryUrl(repo);
        return service;
    }

    private Job job(AppService service, String name, int order, RemoteServer server) {
        Job job = new Job();
        job.setService(service);
        job.setName(name);
        job.setDescription("Sequential job step " + order);
        job.setOrderIndex(order);
        job.setRemoteServer(server);
        return job;
    }

    private Task task(Job job, String name, String command, int group, int order, ExecutionMode mode, String cap) {
        Task task = new Task();
        task.setJob(job);
        task.setName(name);
        task.setCommand(command);
        task.setParallelGroup(group);
        task.setOrderIndex(order);
        task.setExecutionMode(mode);
        task.setCapability(cap);
        return task;
    }

    private Plugin plugin(String name, String description) {
        Plugin plugin = new Plugin();
        plugin.setName(name);
        plugin.setDescription(description);
        plugin.setEnabled(true);
        plugin.setVersion("1.0.0");
        return plugin;
    }

    private JobTemplate template(FeatureModule module, String name, String sample) {
        JobTemplate template = new JobTemplate();
        template.setModule(module);
        template.setName(name);
        template.setDescription("Starter pipeline for " + module.getTitle());
        template.setSampleTasks(sample);
        return template;
    }
}
