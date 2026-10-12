package com.dimitarrradev.workoutScheduler.user.dto;

import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;

public record UserProfileInfoViewModel(
        Integer weight,
        Integer height,
        String gym,
        WorkoutType workoutType
) {

}
