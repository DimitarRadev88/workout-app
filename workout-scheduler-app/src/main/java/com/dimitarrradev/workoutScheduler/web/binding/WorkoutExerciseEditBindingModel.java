package com.dimitarrradev.workoutScheduler.web.binding;

public record WorkoutExerciseEditBindingModel(
        Integer sets,
        Integer minReps,
        Integer maxReps,
        Double weight,
        Integer rest
) {
}
