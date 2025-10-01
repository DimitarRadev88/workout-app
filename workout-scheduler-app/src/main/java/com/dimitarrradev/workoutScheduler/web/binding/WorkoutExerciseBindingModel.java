package com.dimitarrradev.workoutScheduler.web.binding;

public record WorkoutExerciseBindingModel(
        Integer sets,
        Integer minReps,
        Integer maxReps,
        Integer rest,
        Double weight
) {
}
