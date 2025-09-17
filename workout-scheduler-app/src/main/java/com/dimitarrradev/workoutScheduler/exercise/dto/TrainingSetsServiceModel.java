package com.dimitarrradev.workoutScheduler.exercise.dto;

public record TrainingSetsServiceModel(
        Long id,
        String exerciseName,
        Integer count,
        Integer minReps,
        Integer maxReps,
        Double weight,
        Integer rest
) {
}
