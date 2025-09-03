package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.PageAndExerciseFindServiceView;
import com.dimitarrradev.workoutScheduler.exercise.dto.PageAndExerciseReviewServiceView;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseFindBindingModel;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        String username = addUsername(model, authentication);

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
    public String postFindExercises(
            ExerciseFindBindingModel exerciseFind,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("exerciseFind", exerciseFind);
        return "redirect:/exercises/find/" + exerciseFind.targetBodyPart().getName();
    }

    @GetMapping("/add")
    public String getAddExercise(Model model, Authentication authentication) {
        addUsername(model, authentication);

        if (!model.containsAttribute("exerciseAdd")) {
            ExerciseAddBindingModel exerciseAdd = new ExerciseAddBindingModel(null, null, null, null, null);
            model.addAttribute("exerciseAdd", exerciseAdd);
        } else {
            model.addAttribute("exerciseAdd", model.getAttribute("exerciseAdd"));
        }

        return "add-exercise";
    }

    @PostMapping("/add")
    public String addExercise(
            @Valid ExerciseAddBindingModel exerciseAdd,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
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
        addUsername(model, authentication);

        authentication.getAuthorities().stream().filter(authority -> authority
                        .getAuthority().equals("ROLE_ADMIN"))
                .findAny()
                .ifPresent(authority -> {
                    long countForReview = exerciseService.getExercisesForReviewCount();
                    model.addAttribute("countForReview", countForReview);
                });

        return "exercises";
    }

    @GetMapping("/view/{id}")
    public String viewExercise(
            Model model,
            Authentication authentication,
            @PathVariable long id
    ) {
        addUsername(model, authentication);

        ExerciseViewModel exerciseView = exerciseService.getExerciseView(id);
        model.addAttribute("exerciseView", exerciseView);

        return "exercise-view";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/edit/{id}")
    public String getEditExercise(
            @PathVariable long id,
            Model model,
            Authentication authentication
    ) {
        addUsername(model, authentication);

        ExerciseEditBindingModel exerciseEdit = exerciseService.getExerciseEditBindingModel(id);
        model.addAttribute("exerciseEdit", exerciseEdit);

        return "edit-exercise";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/edit/{id}")
    public String editExercise(
            @PathVariable long id,
            ExerciseEditBindingModel exerciseEdit,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("exerciseEdit", exerciseEdit);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.exerciseEdit", bindingResult);
        } else {
            exerciseService.editExercise(exerciseEdit);
        }


        return "redirect:/exercises/edit/" + id;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{exerciseId}/imageUrls/delete/{id}")
    public String deleteImageUrl(
            @PathVariable long exerciseId,
            @PathVariable long id
    ) {
        exerciseService.deleteImageUrl(id);
        return "redirect:/exercises/edit/" + exerciseId;
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
        addUsername(model, authentication);
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/approve/{id}")
    public String approveExercise(@PathVariable long id) {
        exerciseService.approveExercise(id);

        return "redirect:/exercises/for-review";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public String deleteExercise(@PathVariable long id) {
        exerciseService.deleteExercise(id);

        return "redirect:/exercises/for-review";
    }

    private static String addUsername(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);

        return username;
    }

}
