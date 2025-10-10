package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseNameAndIdViewModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.*;
import com.dimitarrradev.workoutScheduler.workout.service.WorkoutService;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutEditServiceModel;
import com.dimitarrradev.workoutScheduler.workoutExercise.service.WorkoutExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private final WorkoutExerciseService workoutExerciseService;

    @GetMapping
    public String getWorkouts() {
        return "workouts";
    }

    @GetMapping("/create")
    public String getWorkoutCreate(Model model) {
        WorkoutAddBindingModel workout;
        if (!model.containsAttribute("workout")) {
            workout = new WorkoutAddBindingModel(null, null, LocalDateTime.now());
        } else {
            workout = (WorkoutAddBindingModel) model.getAttribute("workout");
        }

        TargetBodyPart[] targets = TargetBodyPart.values();
        model.addAttribute("workout", workout);
        model.addAttribute("targets", targets);
        return "workout-create";
    }

    @PostMapping("/create")
    public String postWorkoutCreate(
            @Valid WorkoutAddBindingModel workout,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("workout", workout);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.workout", bindingResult);
            return "redirect:/workouts/create";
        }

        long workoutId = workoutService.createWorkout(workout, authentication.getName());

        return "redirect:/workouts/edit/" + workoutId;
    }

    @GetMapping("/view")
    public String getWorkoutView(Model model, Authentication authentication) {
        List<WorkoutViewServiceModel> workouts = workoutService.getAllByUserUsername(authentication.getName());
        model.addAttribute("workouts", workouts);

        return "workouts-view";
    }

    @GetMapping("/edit/{id}")
    public String getWorkoutEdit(@PathVariable Long id, Model model, Authentication authentication) {
        WorkoutEditServiceModel workoutEditServiceModel = workoutService.getWorkout(id, authentication.getName());
        WorkoutEditBindingModel workout = new WorkoutEditBindingModel(workoutEditServiceModel.workoutType(), workoutEditServiceModel.targetBodyParts());

        List<ExerciseNameAndIdViewModel> exercises = exerciseService.getExercisesForTargetBodyParts(workout.targetBodyParts());
        TargetBodyPart[] targets = TargetBodyPart.values();

        ExerciseWorkoutExerciseBindingModel exerciseWorkoutExerciseBindingModel;
        if (!model.containsAttribute("exerciseWorkoutExerciseBindingModel")) {
            WorkoutExerciseBindingModel workoutExerciseBindingModel;
            if (!model.containsAttribute("workoutExerciseBindingModel")) {
                workoutExerciseBindingModel = new WorkoutExerciseBindingModel(null, null, null, null, null);
            } else {
                workoutExerciseBindingModel = (WorkoutExerciseBindingModel) model.getAttribute("trainingSet");
            }
            exerciseWorkoutExerciseBindingModel = new ExerciseWorkoutExerciseBindingModel(null, workoutExerciseBindingModel);
        } else {
            exerciseWorkoutExerciseBindingModel = (ExerciseWorkoutExerciseBindingModel) model.getAttribute("exerciseWorkoutExerciseBindingModel");
        }

        model.addAttribute("workoutId", id);
        model.addAttribute("workout", workout);
        model.addAttribute("targets", targets);
        model.addAttribute("exercises", exercises);
        model.addAttribute("workoutDate", workoutEditServiceModel.workoutDateTime());
        model.addAttribute("workoutExercises", workoutEditServiceModel.exercises());
        model.addAttribute("exerciseWorkoutExerciseBindingModel", exerciseWorkoutExerciseBindingModel);

        return "workout-edit";
    }

    @PatchMapping("/edit/{id}")
    public String postWorkoutEdit(
            @PathVariable Long id,
            @Valid WorkoutEditBindingModel workout,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (!bindingResult.hasErrors()) {
            workoutService.doEdit(id, workout, authentication.getName());
        }

        return "redirect:/workouts/edit/" + id;
    }

    @PostMapping("/edit/{id}/addExercise")
    public String postAddExerciseToWorkout(
            @PathVariable Long id,
            @Valid ExerciseWorkoutExerciseBindingModel exerciseWorkoutExerciseBindingModel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("exerciseWorkoutExerciseBindingModel", exerciseWorkoutExerciseBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.exerciseWorkoutExerciseBindingModel", bindingResult);
            redirectAttributes.addFlashAttribute("workoutExerciseBindingModel", exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel());
        } else {
            workoutService.addWorkoutExercise(id, exerciseWorkoutExerciseBindingModel, authentication.getName());
        }

        return "redirect:/workouts/edit/" + id;
    }

    @DeleteMapping("/edit/{workoutId}/deleteExercise/{exerciseId}")
    public String deleteWorkoutExercise(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId,
            Authentication authentication
    ) {
        workoutExerciseService.delete(exerciseId, authentication.getName());

        return "redirect:/workouts/edit/" + workoutId;
    }

    @PatchMapping("/{workoutId}/editExercise/{id}")
    public String editExerciseInWorkout(
            @PathVariable Long workoutId,
            @PathVariable Long id,
            WorkoutExerciseEditBindingModel exerciseEdit,
            Authentication authentication
    ) {

        workoutExerciseService.doEdit(id, workoutId, authentication.getName(), exerciseEdit);

        return "redirect:/workouts/edit/" + workoutId;
    }

    @DeleteMapping("/delete/{id}")
    public String deleteWorkout(@PathVariable Long id, Authentication authentication) {
        workoutService.doDelete(authentication.getName(), id);

        return "redirect:/workouts/view";
    }

}
