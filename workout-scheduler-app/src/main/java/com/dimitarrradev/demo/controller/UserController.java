package com.dimitarrradev.demo.controller;

import com.dimitarrradev.demo.controller.dto.UserLoginDto;
import com.dimitarrradev.demo.user.User;
import com.dimitarrradev.demo.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(ModelAndView modelAndView) {
        modelAndView.getModel().put("userLogin", new UserLoginDto("", ""));
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/register")
    public String getHomePage() {
        return "register";
    }

}
