package com.dimitarrradev.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/login")
    public String getLoginPage() {
        return "blank";
    }

    @GetMapping("/register")
    public String getHomePage() {
        return "register";
    }

}
