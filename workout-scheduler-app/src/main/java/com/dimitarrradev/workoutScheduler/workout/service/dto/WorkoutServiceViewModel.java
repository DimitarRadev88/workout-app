package com.dimitarrradev.workoutScheduler.workout.service.dto;

import java.time.LocalDateTime;

public record WorkoutServiceViewModel(
        String targetBodyParts,
        String intensity,
        String volume,
        LocalDateTime workoutDateTime,
        Boolean isCompleted
) {
}
