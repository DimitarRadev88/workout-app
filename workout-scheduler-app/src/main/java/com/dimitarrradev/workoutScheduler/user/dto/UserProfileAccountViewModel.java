package com.dimitarrradev.workoutScheduler.user.dto;

import com.dimitarrradev.workoutScheduler.training.TrainingStyle;

public record UserProfileAccountViewModel(
        String username,
        String email,
        String firstName,
        String lastName
) {
}
