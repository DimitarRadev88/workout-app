package com.dimitarrradev.workoutScheduler.workout.service.dto;

import java.time.LocalDateTime;

public record WorkoutServiceModel(
        LocalDateTime workoutDateTime,
        Boolean isCompleted
) {
}
