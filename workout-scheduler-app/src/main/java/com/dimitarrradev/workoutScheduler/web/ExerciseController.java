package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.dto.PageAndExerciseServiceView;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping("/exercises")
public class ExerciseController {
    private static final Logger log = LoggerFactory.getLogger(ExerciseController.class);
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
        model.addAttribute("sortDirection", sortDirection);

        PageAndExerciseServiceView dataAndExercise = exerciseService.getPaginatedAndSortedDataAndExercise(pageNumber, pageSize, sortDirection);

        model.addAttribute("pageSizes", dataAndExercise.pageSizes());
        model.addAttribute("elementsCount", dataAndExercise.totalElements());
        model.addAttribute("pagesCount", dataAndExercise.totalPages());
        model.addAttribute("exercisesForReview", dataAndExercise.exercises());
        model.addAttribute("shownElements", dataAndExercise.shownElementsRangeAndTotalCountString());

        return "exercises-for-review";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/approve/{id}")
    public String approveExercise(@PathVariable Long id) {
        exerciseService.approveExercise(id);

        return "redirect:/exercises/for-review";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public String deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);

        return "redirect:/exercises/for-review";
    }

}
