package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutAddBindingModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import com.dimitarrradev.workoutScheduler.workout.service.WorkoutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {
    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping
    public String getWorkouts() {
        return "workouts";
    }

    @GetMapping("/add")
    public String getWorkoutsAdd(Model model) {
        WorkoutAddBindingModel workout = new WorkoutAddBindingModel(null, null);
        TargetBodyPart[] targetBodyParts = TargetBodyPart.values();
        model.addAttribute("target", targetBodyParts);
        model.addAttribute("workout", workout);
        return "workout-add";
    }

}
