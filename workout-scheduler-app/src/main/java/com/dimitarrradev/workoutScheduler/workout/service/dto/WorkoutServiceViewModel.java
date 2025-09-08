package com.dimitarrradev.workoutScheduler.workout.service.dto;

public record WorkoutServiceViewModel(
        String targetBodyParts,
        String intensity,
        String volume,
        String timeOfDay,
        Boolean isCompleted
) {
}
