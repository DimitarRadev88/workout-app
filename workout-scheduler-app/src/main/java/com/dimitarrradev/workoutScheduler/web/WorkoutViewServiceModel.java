package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutViewServiceModel(
        Long id,
        WorkoutType workoutType,
        LocalDateTime date,
        String targetBodyParts
) {
}
