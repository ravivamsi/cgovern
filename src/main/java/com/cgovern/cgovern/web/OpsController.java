package com.cgovern.cgovern.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.cgovern.cgovern.domain.FeatureModule;
import com.cgovern.cgovern.domain.JobTemplate;
import com.cgovern.cgovern.domain.Plugin;
import com.cgovern.cgovern.domain.RemoteServer;
import com.cgovern.cgovern.repo.JobRunRepository;
import com.cgovern.cgovern.repo.JobTemplateRepository;
import com.cgovern.cgovern.repo.NotificationRepository;
import com.cgovern.cgovern.repo.PluginRepository;
import com.cgovern.cgovern.service.CatalogService;

@Controller
public class OpsController {

    private final CatalogService catalog;
    private final PluginRepository plugins;
    private final JobTemplateRepository templates;
    private final NotificationRepository notifications;
    private final JobRunRepository runs;

    public OpsController(CatalogService catalog, PluginRepository plugins, JobTemplateRepository templates,
            NotificationRepository notifications, JobRunRepository runs) {
        this.catalog = catalog;
        this.plugins = plugins;
        this.templates = templates;
        this.notifications = notifications;
        this.runs = runs;
    }

    @GetMapping("/servers")
    public String servers(Model model) {
        model.addAttribute("pageTitle", "Remote servers");
        model.addAttribute("servers", catalog.servers());
        return "servers";
    }

    @GetMapping("/servers/new")
    public String newServer(Model model) {
        model.addAttribute("pageTitle", "Register server");
        model.addAttribute("server", new RemoteServer());
        return "server-form";
    }

    @GetMapping("/servers/{id}/edit")
    public String editServer(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Update server");
        model.addAttribute("server", catalog.server(id));
        return "server-form";
    }

    @PostMapping("/servers")
    public String saveServer(@ModelAttribute RemoteServer server) {
        catalog.saveServer(server);
        return "redirect:/servers";
    }

    @PostMapping("/servers/{id}/delete")
    public String deleteServer(@PathVariable Long id) {
        catalog.deleteServer(id);
        return "redirect:/servers";
    }

    @GetMapping("/plugins")
    public String plugins(Model model) {
        model.addAttribute("pageTitle", "Plugins");
        model.addAttribute("plugins", plugins.findAll());
        return "plugins";
    }

    @GetMapping("/plugins/new")
    public String newPlugin(Model model) {
        model.addAttribute("pageTitle", "Add plugin");
        model.addAttribute("plugin", new Plugin());
        return "plugin-form";
    }

    @PostMapping("/plugins")
    public String savePlugin(@ModelAttribute Plugin plugin) {
        plugins.save(plugin);
        return "redirect:/plugins";
    }

    @PostMapping("/plugins/{id}/delete")
    public String deletePlugin(@PathVariable Long id) {
        plugins.deleteById(id);
        return "redirect:/plugins";
    }

    @GetMapping("/templates")
    public String templates(Model model) {
        model.addAttribute("pageTitle", "Job templates");
        model.addAttribute("templates", templates.findAll());
        return "templates";
    }

    @GetMapping("/templates/new")
    public String newTemplate(Model model) {
        model.addAttribute("pageTitle", "Add template");
        model.addAttribute("jobTemplate", new JobTemplate());
        model.addAttribute("modules", FeatureModule.values());
        return "template-form";
    }

    @PostMapping("/templates")
    public String saveTemplate(@ModelAttribute JobTemplate jobTemplate) {
        templates.save(jobTemplate);
        return "redirect:/templates";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("pageTitle", "Notifications");
        model.addAttribute("notifications", notifications.findAllByOrderByCreatedAtDesc());
        model.addAttribute("runs", runs.findAll());
        return "notifications";
    }
}
