package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;

import java.time.LocalDateTime;

public record WorkoutViewServiceModel(
        Long id,
        WorkoutType workoutType,
        LocalDateTime date,
        String targetBodyParts
) {
}
