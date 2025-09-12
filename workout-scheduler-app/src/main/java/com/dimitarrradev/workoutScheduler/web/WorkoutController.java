package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutAddBindingModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.service.WorkoutService;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutEditServiceModel;
import jakarta.validation.Valid;
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

@Controller
@RequestMapping("/workouts")
public class WorkoutController {
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

    @GetMapping("/edit/{id}")
    public String getWorkoutEdit(@PathVariable long id, Model model) {
        WorkoutEditServiceModel workoutServiceModel = workoutService.getWorkout(id);
        WorkoutAddBindingModel workout = new WorkoutAddBindingModel(workoutServiceModel.workoutType(), workoutServiceModel.targetBodyParts(), workoutServiceModel.workoutDateTime());

        model.addAttribute("workout", workout);

        return "workout-edit";
    }

}
