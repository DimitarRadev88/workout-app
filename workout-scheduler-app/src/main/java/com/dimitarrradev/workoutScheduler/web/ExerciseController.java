package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.dto.*;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/find/{muscleGroup}")
    public String getFindExercises(
            Model model,
            @PathVariable("muscleGroup") String muscleGroup,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "name", defaultValue = "") String name
    ) {
        ExerciseFindBindingModel exerciseFind;

        if (!model.containsAttribute("exerciseFind")) {
            exerciseFind = new ExerciseFindBindingModel(name, TargetBodyPart.valueOf(muscleGroup.toUpperCase()), null, null);
        } else {
            exerciseFind = (ExerciseFindBindingModel) model.getAttribute("exerciseFind");
        }

        model.addAttribute("name", name);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("muscleGroup", muscleGroup);

        Page<ExerciseFindViewModel> activeExercisesPage = exerciseService.findActiveExercisesPage(exerciseFind, pageNumber, pageSize, sortDirection);
        PageInformation pageInformation = exerciseService.getPageInfo(activeExercisesPage);

        List<ExerciseFindViewModel> allActiveExercises = exerciseService.getAllActiveExercises();

        model.addAttribute("pageSizes", pageInformation.pageSizes());
        model.addAttribute("elementsCount", activeExercisesPage.getTotalElements());
        model.addAttribute("pagesCount", activeExercisesPage.getTotalPages());
        model.addAttribute("exercises", activeExercisesPage.getContent());
        model.addAttribute("activeExercises", allActiveExercises);
        model.addAttribute("shownElements", pageInformation.shownElementsRangeAndTotalCountString());
        model.addAttribute("exerciseFind", exerciseFind);

        return "exercise-find";
    }

    @PostMapping("/find/{muscleGroup}")
    public String postFindExercises(ExerciseFindBindingModel exerciseFind, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exerciseFind", exerciseFind);
        return "redirect:/exercises/find/" + exerciseFind.targetBodyPart().getName();
    }

    @GetMapping("/add")
    public String getAddExercise(Model model) {
        if (!model.containsAttribute("exerciseAdd")) {
            ExerciseAddBindingModel exerciseAdd = new ExerciseAddBindingModel(null, null, null, null, null, null);
            model.addAttribute("exerciseAdd", exerciseAdd);
        } else {
            model.addAttribute("exerciseAdd", model.getAttribute("exerciseAdd"));
        }

        return "exercise-add";
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
            String message = exerciseService.addExerciseForReview(exerciseAdd);
            redirectAttributes.addFlashAttribute("message", message);
        }

        return "redirect:/exercises";
    }

    @GetMapping("")
    public String getExercises(Model model, Authentication authentication) {
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
    public String viewExercise(Model model, @PathVariable long id) {
        ExerciseViewModel exerciseView = exerciseService.getExerciseView(id);
        model.addAttribute("exerciseView", exerciseView);

        return "exercise-view";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/edit/{id}")
    public String getEditExercise(@PathVariable long id, Model model) {
        if (!model.containsAttribute("exerciseEdit")) {
            ExerciseEditBindingModel exerciseEdit = exerciseService.getExerciseEditBindingModel(id);
            model.addAttribute("exerciseEdit", exerciseEdit);
        } else {
            model.addAttribute("exerciseEdit", model.getAttribute("exerciseEdit"));
        }

        model.addAttribute("imageUrls", exerciseService.getExerciseImages(id));

        return "exercise-edit";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/edit/{id}")
    public String editExercise(
            @PathVariable long id,
            @Valid ExerciseEditBindingModel exerciseEdit,
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
    public String deleteImageUrl(@PathVariable long exerciseId, @PathVariable long id) {
        exerciseService.deleteImageUrl(id);
        return "redirect:/exercises/edit/" + exerciseId;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/for-review")
    public String getExercisesForReview(
            Model model,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortDirection", sortDirection);

        Page<ExerciseForReviewViewModel> exercises = exerciseService.getExercisesForReviewPage(pageNumber, pageSize, sortDirection);
        PageInformation pageInfo = exerciseService.getPageInfo(exercises);

        model.addAttribute("pageSizes", pageInfo.pageSizes());
        model.addAttribute("elementsCount", exercises.getTotalElements());
        model.addAttribute("pagesCount", exercises.getTotalPages());
        model.addAttribute("exercisesForReview", exercises.getContent());
        model.addAttribute("shownElements", pageInfo.shownElementsRangeAndTotalCountString());

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

}
