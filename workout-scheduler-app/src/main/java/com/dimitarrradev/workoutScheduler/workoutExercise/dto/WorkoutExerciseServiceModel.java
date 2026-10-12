package com.dimitarrradev.workoutScheduler.workoutExercise.dto;

public record WorkoutExerciseServiceModel(
        Long id,
        String exerciseName,
        Integer sets,
        Integer minReps,
        Integer maxReps,
        Double weight,
        Integer rest
) {
}
