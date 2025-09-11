package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseNameAndIdViewModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutAddBindingModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import com.dimitarrradev.workoutScheduler.workout.service.WorkoutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/add")
    public String getWorkoutsAdd(Model model, @RequestParam(name = "selectedBodyParts", defaultValue = "ALL") String[] selectedBodyParts) {
        List<ExerciseNameAndIdViewModel> exercises = exerciseService.getExercisesViewByTargets(selectedBodyParts);
        TargetBodyPart[] target = TargetBodyPart.values();
        List<TargetBodyPart> selectedBodyPart = Arrays.stream(selectedBodyParts).map(TargetBodyPart::valueOf).toList();
        WorkoutAddBindingModel workout = new WorkoutAddBindingModel(null, selectedBodyPart, null);
        model.addAttribute("target", target);
        model.addAttribute("workout", workout);
        model.addAttribute("exercises", exercises);
        return "workout-add";
    }

}
