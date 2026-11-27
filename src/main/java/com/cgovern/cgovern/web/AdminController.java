package com.cgovern.cgovern.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cgovern.cgovern.domain.OrgRole;
import com.cgovern.cgovern.service.AccessService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AccessService access;

    public AdminController(AccessService access) {
        this.access = access;
    }

    @GetMapping
    public String console(Model model) {
        model.addAttribute("pageTitle", "Admin console");
        model.addAttribute("users", access.users());
        model.addAttribute("organizations", access.organizations());
        model.addAttribute("roles", OrgRole.values());
        return "admin/console";
    }

    @GetMapping("/orgs/{orgId}")
    public String orgAccess(@PathVariable Long orgId, Model model) {
        model.addAttribute("pageTitle", "Org access");
        model.addAttribute("organization", access.organization(orgId));
        model.addAttribute("members", access.membershipsForOrg(orgId));
        model.addAttribute("roles", OrgRole.values());
        return "admin/org-access";
    }

    @PostMapping("/users/{userId}/role")
    public String platformRole(@PathVariable Long userId, @RequestParam String role, RedirectAttributes redirect) {
        access.setPlatformRole(userId, role);
        redirect.addFlashAttribute("message", "Updated platform role.");
        return "redirect:/admin";
    }

    @PostMapping("/orgs/{orgId}/members")
    public String grant(@PathVariable Long orgId, @RequestParam String email, @RequestParam OrgRole role,
            RedirectAttributes redirect) {
        try {
            access.grant(orgId, email, role);
            redirect.addFlashAttribute("message", "Access granted.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/orgs/" + orgId;
    }

    @PostMapping("/members/{membershipId}/role")
    public String changeRole(@PathVariable Long membershipId, @RequestParam OrgRole role,
            @RequestParam Long orgId, RedirectAttributes redirect) {
        access.changeOrgRole(membershipId, role);
        redirect.addFlashAttribute("message", "Org role updated.");
        return "redirect:/admin/orgs/" + orgId;
    }

    @PostMapping("/members/{membershipId}/revoke")
    public String revoke(@PathVariable Long membershipId, @RequestParam Long orgId, RedirectAttributes redirect) {
        access.revoke(membershipId);
        redirect.addFlashAttribute("message", "Access removed.");
        return "redirect:/admin/orgs/" + orgId;
    }
}
