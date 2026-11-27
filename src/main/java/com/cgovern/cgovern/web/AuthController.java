package com.cgovern.cgovern.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cgovern.cgovern.service.AccountService;

@Controller
public class AuthController {

    private final AccountService accounts;

    public AuthController(AccountService accounts) {
        this.accounts = accounts;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Sign in");
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Create account");
        return "register";
    }

    @PostMapping("/register")
    public String createAccount(@RequestParam String fullName, @RequestParam String email,
            @RequestParam String password, Model model) {
        try {
            accounts.register(fullName, email, password);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageTitle", "Create account");
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            return "register";
        }
    }
}
