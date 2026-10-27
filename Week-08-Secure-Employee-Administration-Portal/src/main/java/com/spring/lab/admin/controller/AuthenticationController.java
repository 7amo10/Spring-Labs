package com.spring.lab.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller handling public authentication views and access-denied pages.
 */
@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/staff/list";
    }
}
