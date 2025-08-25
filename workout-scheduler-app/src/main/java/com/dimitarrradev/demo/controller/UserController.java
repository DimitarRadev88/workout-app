package com.dimitarrradev.demo.controller;

import com.dimitarrradev.demo.controller.model.UserLoginBindingModel;
import com.dimitarrradev.demo.controller.model.UserRegisterBindingModel;
import com.dimitarrradev.demo.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String getHomePage(Model model) {
        if (!model.containsAttribute("userRegister")) {
            model.addAttribute("userRegister", new UserRegisterBindingModel("", "", "", ""));
        }

        return "register";
    }

    @PostMapping("/register")
    public ModelAndView registerUser(ModelAndView modelAndView, @Valid @ModelAttribute("userRegister") UserRegisterBindingModel userRegister, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userRegister", userRegister);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegister", bindingResult);
            modelAndView.setViewName("redirect:/users/register");
        } else {
            userService.doRegister(userRegister);
            modelAndView.setViewName("redirect:/users/login");
        }

        return modelAndView;
    }

}
