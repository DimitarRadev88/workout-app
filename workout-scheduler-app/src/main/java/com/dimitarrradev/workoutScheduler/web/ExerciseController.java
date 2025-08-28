package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseForReviewViewModel;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

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

        ExerciseAddBindingModel exerciseAdd = new ExerciseAddBindingModel(null, null, null, null, username, null);
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

        authentication.getAuthorities().stream().filter(authority -> authority
                .getAuthority().equals("ROLE_ADMIN"))
                .findAny()
                .ifPresent(authority -> {
                    long countForReview = exerciseService.getExercisesForReviewCount();
                    model.addAttribute("countForReview", countForReview);
                });

        return "exercises";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/for-review")
    public String getExercisesForReview(
            Model model,
            Authentication authentication,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pageSizes", Arrays.asList(5, 10, 25, 50));

        Page<ExerciseForReviewViewModel> allForReview = exerciseService.getPaginatedAndSorted(pageNumber, pageSize, sortDirection);
        long elementsCount = allForReview.getTotalElements();
        long pagesCount = elementsCount / pageSize;
        List<ExerciseForReviewViewModel> exercisesForReview = allForReview.getContent();

        model.addAttribute("elementsCount", elementsCount);
        model.addAttribute("pagesCount", pagesCount);
        model.addAttribute("exercisesForReview", exercisesForReview);

        return "exercises-for-review";
    }

}
