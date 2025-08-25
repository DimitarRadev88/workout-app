package com.dimitarrradev.demo.controller;

import com.dimitarrradev.demo.controller.model.UserLoginBindingModel;
import com.dimitarrradev.demo.controller.model.UserRegisterBindingModel;
import com.dimitarrradev.demo.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(ModelAndView modelAndView) {
        modelAndView.getModel().put("userLogin", new UserLoginBindingModel(null, null));
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getHomePage(ModelAndView modelAndView) {
        modelAndView.getModel().put("userRegister", new UserRegisterBindingModel(null, null, null, null));
        modelAndView.setViewName("register");

        return modelAndView;
    }

    @PostMapping("/register")
    public String registerUser(UserRegisterBindingModel userRegisterBindingModel) {
        userService.doRegister(userRegisterBindingModel);

        return "redirect:/users/login";
    }

}
