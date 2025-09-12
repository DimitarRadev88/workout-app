package com.dimitarrradev.workoutScheduler.exercise.dto;

public record TrainingSetsServiceModel(
        Long id,
        String exerciseName,
        Integer sets,
        Integer minReps,
        Integer maxReps,
        Double weight,
        Integer rest
) {
}
