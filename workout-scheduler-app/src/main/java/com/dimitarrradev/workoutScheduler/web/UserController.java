package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.user.service.UserService;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileAccountViewModel;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileInfoViewModel;
import com.dimitarrradev.workoutScheduler.web.binding.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/profile")
    public String getProfilePage(Model model, Authentication authentication) {
        String username = authentication.getName();
        if (!model.containsAttribute("profileAccountEdit")) {
            UserProfileAccountViewModel userProfile = userService.getUserProfileAccountView(username);

            UserProfileAccountEditBindingModel profileAccountEdit = new UserProfileAccountEditBindingModel(
                    userProfile.username(),
                    userProfile.email(),
                    userProfile.firstName(),
                    userProfile.lastName(),
                    Boolean.FALSE
            );

            model.addAttribute("profileAccountEdit", profileAccountEdit);

        }

        if (!model.containsAttribute("profileInfoEdit")) {
            UserProfileInfoViewModel userProfile = userService.getUserProfileInfoView(username);

            UserProfileInfoEditBindingModel profileInfoEdit = new UserProfileInfoEditBindingModel(
                    userProfile.weight(),
                    userProfile.height(),
                    userProfile.gym(),
                    userProfile.trainingStyle()
            );

            model.addAttribute("profileInfoEdit", profileInfoEdit);
        }

        if (!model.containsAttribute("passwordChange")) {
            UserProfilePasswordChangeBindingModel passwordChange = new UserProfilePasswordChangeBindingModel(
                    null,
                    null,
                    null
            );

            model.addAttribute("passwordChange", passwordChange);
        }

        return "profile";
    }

    @PatchMapping("/profile-account-edit")
    public String postProfileAccountEdit(Authentication authentication, @Valid @ModelAttribute("profileAccountEdit") UserProfileAccountEditBindingModel profileAccountEdit, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("profileAccountEdit", profileAccountEdit);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileAccountEdit", bindingResult);
        } else {
            userService.doAccountEdit(authentication.getName(), profileAccountEdit);
        }

        return "redirect:/users/profile";
    }

    @PutMapping("/profile-password-change")
    public String postProfileChangePassword(Authentication authentication, @Valid @ModelAttribute("passwordChange") UserProfilePasswordChangeBindingModel passwordChange, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            passwordChange = new UserProfilePasswordChangeBindingModel(
                    null,
                    null,
                    null
            );
            redirectAttributes.addFlashAttribute("passwordChange", passwordChange);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordChange", bindingResult);
        } else {
            userService.doPasswordChange(authentication.getName(), passwordChange);
        }

        return "redirect:/users/profile";
    }

    @PatchMapping("/profile-info-edit")
    public String postProfileInfoEdit(Authentication authentication, @Valid @ModelAttribute("profileInfoEdit") UserProfileInfoEditBindingModel profileInfoEdit, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("profileInfoEdit", profileInfoEdit);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileInfoEdit", bindingResult);
        } else {
            userService.doInfoEdit(authentication.getName(), profileInfoEdit);
        }
        return "redirect:/users/profile";
    }

    @PostMapping("/logout")
    public String logout(Authentication authentication) {
        authentication.setAuthenticated(false);
        return "redirect:/users/login";
    }

}
