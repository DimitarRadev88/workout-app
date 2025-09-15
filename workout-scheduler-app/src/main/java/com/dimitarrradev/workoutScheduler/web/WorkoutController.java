package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseNameAndIdViewModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.*;
import com.dimitarrradev.workoutScheduler.workout.service.WorkoutService;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutEditServiceModel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {
    private static final Logger log = LoggerFactory.getLogger(WorkoutController.class);
    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;

    public WorkoutController(WorkoutService workoutService, ExerciseService exerciseService) {
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
    }

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
    public String getWorkoutEdit(@PathVariable Long id, Model model) {
        WorkoutEditServiceModel workoutEditServiceModel = workoutService.getWorkout(id);
        WorkoutEditBindingModel workout = new WorkoutEditBindingModel(workoutEditServiceModel.workoutType(), workoutEditServiceModel.targetBodyParts());
        List<ExerciseNameAndIdViewModel> exercises = exerciseService.getExercisesForTargetBodyParts(workout.targetBodyParts());
        TargetBodyPart[] targets = TargetBodyPart.values();

        ExerciseTrainingSetsBindingModel exerciseTrainingSetsBindingModel = new ExerciseTrainingSetsBindingModel(null, new TrainingSetBindingModel(null, null, null, null, null));
        model.addAttribute("workoutId", id);
        model.addAttribute("workout", workout);
        model.addAttribute("targets", targets);
        model.addAttribute("exercises", exercises);
        model.addAttribute("workoutDate", workoutEditServiceModel.workoutDateTime());
        model.addAttribute("workoutExercises", workoutEditServiceModel.exercises());
        model.addAttribute("exerciseTrainingSetsBindingModel", exerciseTrainingSetsBindingModel);

        return "workout-edit";
    }

    @PostMapping("/edit/{id}/addExercise")
    public String postBindExerciseToWorkout(
            @PathVariable Long id,
            ExerciseTrainingSetsBindingModel exerciseTrainingSetsBindingModel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("exerciseTrainingSetsBindingModel", exerciseTrainingSetsBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.exerciseTrainingSetsBindingModel", bindingResult);
        } else {
            workoutService.addExerciseAndTrainingSetsToWorkout(id, exerciseTrainingSetsBindingModel);
        }

        return  "redirect:/workouts/edit/" + id;
    }

}
