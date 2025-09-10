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
    public String getWorkoutsAdd(Model model, @RequestParam(name = "selectedBodyParts", defaultValue = "All") String[] selectedBodyParts) {
        WorkoutAddBindingModel workout = new WorkoutAddBindingModel(null, null, null);
        List<ExerciseNameAndIdViewModel> exercises = exerciseService.getExercisesViewByTargets(selectedBodyParts);
        TargetBodyPart[] target = TargetBodyPart.values();
        List<String> selectedBodyPartNames = Arrays.asList(selectedBodyParts);
        System.out.println(selectedBodyPartNames.contains(target[0].getName().toUpperCase()));
        model.addAttribute("target", target);
        model.addAttribute("selectedBodyPartNames", selectedBodyPartNames);
        model.addAttribute("workout", workout);
        model.addAttribute("exercises", exercises);
        return "workout-add";
    }

}
