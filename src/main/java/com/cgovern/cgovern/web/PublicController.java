package com.cgovern.cgovern.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cgovern.cgovern.domain.FeatureModule;
import com.cgovern.cgovern.domain.SupportTicket;
import com.cgovern.cgovern.repo.SupportTicketRepository;

@Controller
public class PublicController {

    private final SupportTicketRepository tickets;

    public PublicController(SupportTicketRepository tickets) {
        this.tickets = tickets;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("modules", FeatureModule.values());
        model.addAttribute("pageTitle", "CGovern control plane");
        return "home";
    }

    @GetMapping("/pricing")
    public String pricing(Model model) {
        model.addAttribute("pageTitle", "Pricing");
        return "pricing";
    }

    @GetMapping("/api-docs")
    public String apiDocs(Model model) {
        model.addAttribute("pageTitle", "API documentation");
        model.addAttribute("modules", FeatureModule.values());
        return "api-docs";
    }

    @GetMapping("/support")
    public String support(Model model) {
        model.addAttribute("pageTitle", "Support");
        model.addAttribute("ticket", new SupportTicket());
        return "support";
    }

    @PostMapping("/support")
    public String submitSupport(@ModelAttribute SupportTicket ticket, RedirectAttributes redirect) {
        tickets.save(ticket);
        redirect.addFlashAttribute("message", "Thanks. Support received your note.");
        return "redirect:/support";
    }
}
