package com.cgovern.cgovern.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cgovern.cgovern.domain.AppService;
import com.cgovern.cgovern.domain.ExecutionMode;
import com.cgovern.cgovern.domain.FeatureModule;
import com.cgovern.cgovern.domain.Job;
import com.cgovern.cgovern.domain.Organization;
import com.cgovern.cgovern.domain.Task;
import com.cgovern.cgovern.service.CatalogService;
import com.cgovern.cgovern.service.JobExecutionService;

@Controller
@RequestMapping("/features/{module}")
public class FeatureCatalogController {

    private final CatalogService catalog;
    private final JobExecutionService executions;

    public FeatureCatalogController(CatalogService catalog, JobExecutionService executions) {
        this.catalog = catalog;
        this.executions = executions;
    }

    @InitBinder
    public void ignorePathModuleOnEntities(WebDataBinder binder) {
        binder.setDisallowedFields("module");
    }

    @GetMapping("/orgs")
    public String orgs(@PathVariable String module, Model model) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        populate(model, feature);
        model.addAttribute("orgs", catalog.orgs(feature));
        return "features/orgs";
    }

    @GetMapping("/orgs/new")
    public String newOrg(@PathVariable String module, Model model) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        populate(model, feature);
        Organization organization = new Organization();
        organization.setModule(feature);
        model.addAttribute("organization", organization);
        return "features/org-form";
    }

    @GetMapping("/orgs/{orgId}/edit")
    public String editOrg(@PathVariable String module, @PathVariable Long orgId, Model model) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        populate(model, feature);
        model.addAttribute("organization", catalog.org(orgId));
        return "features/org-form";
    }

    @PostMapping("/orgs")
    public String saveOrg(@PathVariable String module, @ModelAttribute Organization organization) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        organization.setModule(feature);
        catalog.saveOrg(organization);
        return "redirect:/features/" + module + "/orgs";
    }

    @PostMapping("/orgs/{orgId}/delete")
    public String deleteOrg(@PathVariable String module, @PathVariable Long orgId) {
        catalog.deleteOrg(orgId);
        return "redirect:/features/" + module + "/orgs";
    }

    @GetMapping("/orgs/{orgId}/services")
    public String services(@PathVariable String module, @PathVariable Long orgId, Model model) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        Organization organization = catalog.org(orgId);
        populate(model, feature);
        model.addAttribute("organization", organization);
        model.addAttribute("services", catalog.services(orgId));
        return "features/services";
    }

    @GetMapping("/orgs/{orgId}/services/new")
    public String newService(@PathVariable String module, @PathVariable Long orgId, Model model) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        populate(model, feature);
        model.addAttribute("organization", catalog.org(orgId));
        model.addAttribute("appService", new AppService());
        return "features/service-form";
    }

    @GetMapping("/orgs/{orgId}/services/{serviceId}/edit")
    public String editService(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId,
            Model model) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        populate(model, feature);
        model.addAttribute("organization", catalog.org(orgId));
        model.addAttribute("appService", catalog.service(serviceId));
        return "features/service-form";
    }

    @PostMapping("/orgs/{orgId}/services")
    public String saveService(@PathVariable String module, @PathVariable Long orgId, @ModelAttribute AppService appService) {
        appService.setOrganization(catalog.org(orgId));
        catalog.saveService(appService);
        return "redirect:/features/" + module + "/orgs/" + orgId + "/services";
    }

    @PostMapping("/orgs/{orgId}/services/{serviceId}/delete")
    public String deleteService(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId) {
        catalog.deleteService(serviceId);
        return "redirect:/features/" + module + "/orgs/" + orgId + "/services";
    }

    @GetMapping("/orgs/{orgId}/services/{serviceId}/jobs")
    public String jobs(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId, Model model) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        populate(model, feature);
        model.addAttribute("organization", catalog.org(orgId));
        model.addAttribute("appService", catalog.service(serviceId));
        model.addAttribute("jobs", catalog.jobs(serviceId));
        return "features/jobs";
    }

    @GetMapping("/orgs/{orgId}/services/{serviceId}/jobs/new")
    public String newJob(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId, Model model) {
        jobForm(model, module, orgId, serviceId, new Job());
        return "features/job-form";
    }

    @GetMapping("/orgs/{orgId}/services/{serviceId}/jobs/{jobId}/edit")
    public String editJob(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId,
            @PathVariable Long jobId, Model model) {
        jobForm(model, module, orgId, serviceId, catalog.job(jobId));
        return "features/job-form";
    }

    @PostMapping("/orgs/{orgId}/services/{serviceId}/jobs")
    public String saveJob(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId,
            @ModelAttribute Job job, @RequestParam(required = false) Long remoteServerId) {
        job.setService(catalog.service(serviceId));
        if (remoteServerId != null) {
            job.setRemoteServer(catalog.server(remoteServerId));
        }
        catalog.saveJob(job);
        return "redirect:/features/" + module + "/orgs/" + orgId + "/services/" + serviceId + "/jobs";
    }

    @PostMapping("/orgs/{orgId}/services/{serviceId}/jobs/{jobId}/delete")
    public String deleteJob(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId,
            @PathVariable Long jobId) {
        catalog.deleteJob(jobId);
        return "redirect:/features/" + module + "/orgs/" + orgId + "/services/" + serviceId + "/jobs";
    }

    @PostMapping("/orgs/{orgId}/services/{serviceId}/run")
    public String runPipeline(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId) {
        executions.runServicePipeline(serviceId);
        return "redirect:/features/" + module + "/orgs/" + orgId + "/services/" + serviceId + "/jobs";
    }

    @GetMapping("/orgs/{orgId}/services/{serviceId}/jobs/{jobId}/tasks")
    public String tasks(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId,
            @PathVariable Long jobId, Model model) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        populate(model, feature);
        model.addAttribute("organization", catalog.org(orgId));
        model.addAttribute("appService", catalog.service(serviceId));
        model.addAttribute("job", catalog.job(jobId));
        model.addAttribute("tasks", catalog.tasks(jobId));
        return "features/tasks";
    }

    @GetMapping("/orgs/{orgId}/services/{serviceId}/jobs/{jobId}/tasks/new")
    public String newTask(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId,
            @PathVariable Long jobId, Model model) {
        taskForm(model, module, orgId, serviceId, jobId, new Task());
        return "features/task-form";
    }

    @GetMapping("/orgs/{orgId}/services/{serviceId}/jobs/{jobId}/tasks/{taskId}/edit")
    public String editTask(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId,
            @PathVariable Long jobId, @PathVariable Long taskId, Model model) {
        taskForm(model, module, orgId, serviceId, jobId, catalog.task(taskId));
        return "features/task-form";
    }

    @PostMapping("/orgs/{orgId}/services/{serviceId}/jobs/{jobId}/tasks")
    public String saveTask(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId,
            @PathVariable Long jobId, @ModelAttribute Task task) {
        task.setJob(catalog.job(jobId));
        catalog.saveTask(task);
        return "redirect:/features/" + module + "/orgs/" + orgId + "/services/" + serviceId + "/jobs/" + jobId + "/tasks";
    }

    @PostMapping("/orgs/{orgId}/services/{serviceId}/jobs/{jobId}/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId,
            @PathVariable Long jobId, @PathVariable Long taskId) {
        catalog.deleteTask(taskId);
        return "redirect:/features/" + module + "/orgs/" + orgId + "/services/" + serviceId + "/jobs/" + jobId + "/tasks";
    }

    @PostMapping("/orgs/{orgId}/services/{serviceId}/jobs/{jobId}/run")
    public String runJob(@PathVariable String module, @PathVariable Long orgId, @PathVariable Long serviceId,
            @PathVariable Long jobId) {
        executions.executeJob(catalog.job(jobId));
        return "redirect:/features/" + module + "/orgs/" + orgId + "/services/" + serviceId + "/jobs/" + jobId + "/tasks";
    }

    private void jobForm(Model model, String module, Long orgId, Long serviceId, Job job) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        populate(model, feature);
        model.addAttribute("organization", catalog.org(orgId));
        model.addAttribute("appService", catalog.service(serviceId));
        model.addAttribute("job", job);
        model.addAttribute("servers", catalog.servers());
    }

    private void taskForm(Model model, String module, Long orgId, Long serviceId, Long jobId, Task task) {
        FeatureModule feature = FeatureModule.fromSlug(module);
        populate(model, feature);
        model.addAttribute("organization", catalog.org(orgId));
        model.addAttribute("appService", catalog.service(serviceId));
        model.addAttribute("job", catalog.job(jobId));
        model.addAttribute("task", task);
        model.addAttribute("modes", ExecutionMode.values());
    }

    private void populate(Model model, FeatureModule feature) {
        model.addAttribute("feature", feature);
        model.addAttribute("pageTitle", feature.getTitle());
        model.addAttribute("modules", FeatureModule.values());
    }
}
