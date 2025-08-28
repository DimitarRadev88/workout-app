package com.dimitarrradev.workoutScheduler.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getHome(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        return "index";
    }


}
