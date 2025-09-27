package org.migranthealth.Base.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String index() {
        return "redirect:/login.html";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login.html";
    }
    
    @GetMapping("/signup")
    public String signup() {
        return "signup.html";
    }
    
    @GetMapping("/home")
    public String home() {
        return "home.html";
    }
    
    @GetMapping("/balance")
    public String balance() {
        return "balance.html";
    }
}