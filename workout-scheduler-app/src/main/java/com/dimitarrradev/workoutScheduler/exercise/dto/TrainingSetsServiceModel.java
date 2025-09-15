package com.dimitarrradev.workoutScheduler.exercise.dto;

public record TrainingSetsServiceModel(
        Long id,
        String exerciseName,
        Long sets,
        Integer minReps,
        Integer maxReps,
        Double weight,
        Integer rest
) {
}
