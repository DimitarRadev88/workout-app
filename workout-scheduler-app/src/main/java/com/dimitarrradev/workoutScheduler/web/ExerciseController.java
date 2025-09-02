package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.PageAndExerciseFindServiceView;
import com.dimitarrradev.workoutScheduler.exercise.dto.PageAndExerciseReviewServiceView;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseFindBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ImageUrlBindingModel;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/find/{muscleGroup}")
    public String getFindExercises(
            Model model,
            Authentication authentication,
            @PathVariable("muscleGroup") String muscleGroup,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        String username = authentication.getName();
        model.addAttribute("username", username);

        ExerciseFindBindingModel exerciseFind;

        if (!model.containsAttribute("exerciseFind")) {
            exerciseFind = new ExerciseFindBindingModel(TargetBodyPart.valueOf(muscleGroup.toUpperCase()), null);
        } else {
            exerciseFind = (ExerciseFindBindingModel) model.getAttribute("exerciseFind");
        }

        model.addAttribute("username", username);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("muscleGroup", muscleGroup);

        PageAndExerciseFindServiceView dataAndExerciseViewModel = exerciseService.getPaginatedAndSortedDataAndExerciseActiveTrue(exerciseFind, pageNumber, pageSize, sortDirection);

        model.addAttribute("pageSizes", dataAndExerciseViewModel.pageSizes());
        model.addAttribute("elementsCount", dataAndExerciseViewModel.totalElements());
        model.addAttribute("pagesCount", dataAndExerciseViewModel.totalPages());
        model.addAttribute("exercises", dataAndExerciseViewModel.exercises());
        model.addAttribute("shownElements", dataAndExerciseViewModel.shownElementsRangeAndTotalCountString());
        model.addAttribute("exerciseFind", exerciseFind);

        return "find-exercise";
    }

    @PostMapping("/find/{muscleGroup}")
    public String postFindExercises(ExerciseFindBindingModel exerciseFind, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exerciseFind", exerciseFind);
        return "redirect:/exercises/find/" + exerciseFind.targetBodyPart().getName();
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

        PageAndExerciseReviewServiceView dataAndExercise = exerciseService.getPaginatedAndSortedDataAndExerciseActiveFalse(pageNumber, pageSize, sortDirection);

        model.addAttribute("pageSizes", dataAndExercise.pageSizes());
        model.addAttribute("elementsCount", dataAndExercise.totalElements());
        model.addAttribute("pagesCount", dataAndExercise.totalPages());
        model.addAttribute("exercisesForReview", dataAndExercise.exercises());
        model.addAttribute("shownElements", dataAndExercise.shownElementsRangeAndTotalCountString());

        return "exercises-for-review";
    }

    @GetMapping("/view/{id}")
    public String viewExercise(Model model, Authentication authentication, @PathVariable long id) {
        String username = authentication.getName();
        model.addAttribute("username", username);

        ExerciseViewModel exerciseView = exerciseService.getExerciseView(id);
        model.addAttribute("exerciseView", exerciseView);


        ExerciseEditBindingModel exerciseEdit = new ExerciseEditBindingModel(
                id,
                exerciseView.name(),
                exerciseView.description(),
                null,
                exerciseView.imageUrls().stream()
                        .map(imageUrlViewModel -> new ImageUrlBindingModel(
                                imageUrlViewModel.id(),
                                imageUrlViewModel.url(),
                                imageUrlViewModel.forDelete()))
                        .toList()
        );


        model.addAttribute("exerciseEdit", exerciseEdit);

        return "exercise-view";
    }

    @PatchMapping("/edit/{id}")
    public String editExercise(@PathVariable long id, ExerciseEditBindingModel exerciseEdit, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exerciseEdit", exerciseEdit);

        exerciseService.editExercise(exerciseEdit);

        return "redirect:/exercises/view/" + id;
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
