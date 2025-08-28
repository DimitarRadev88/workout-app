package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/find")
    public String getFindExercises(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        return "find-exercise";
    }

    @GetMapping("/add")
    public String getAddExercise(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);

        ExerciseAddBindingModel exerciseAdd = new ExerciseAddBindingModel(null, null, null, null);
        model.addAttribute("exerciseAdd", exerciseAdd);

        return "add-exercise";
    }

    @PostMapping("/add")
    public String addExercise(@Valid @ModelAttribute("exerciseAdd") ExerciseAddBindingModel exerciseAdd, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("exerciseAdd", exerciseAdd);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.exerciseAdd", bindingResult);
            return "redirect:/exercises/add";
        } else {
            exerciseService.addExerciseForReview(exerciseAdd);
        }

        return "redirect:/exercises";
    }

    @GetMapping
    public String getExercises(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        return "exercises";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/for-review")
    public String getExercisesForReview(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);

        return "exercises-for-review";
    }

}
