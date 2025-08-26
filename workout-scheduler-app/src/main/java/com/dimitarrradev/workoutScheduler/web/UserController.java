package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.user.UserService;
import com.dimitarrradev.workoutScheduler.web.model.UserLoginBindingModel;
import com.dimitarrradev.workoutScheduler.web.model.UserRegisterBindingModel;
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
    public String getLoginPage(Model model) {
        if (!model.containsAttribute("userLogin")) {
            model.addAttribute("userLogin", new UserLoginBindingModel(null, null));
        }

        return "login";
    }

    @GetMapping("/register")
    public String getHomePage(Model model) {
        if (!model.containsAttribute("userRegister")) {
            model.addAttribute("userRegister", new UserRegisterBindingModel(null, null, null, null));
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
            String username = userService.doRegister(userRegister);
            redirectAttributes.addFlashAttribute("userLogin", new UserLoginBindingModel(username, null));
            modelAndView.setViewName("redirect:/users/login");
        }

        return modelAndView;
    }

}
