package com.dimitarrradev.workoutScheduler.user.dto;

import com.dimitarrradev.workoutScheduler.training.TrainingStyle;

public record UserProfileViewModel(
        String username,
        String email,
        String firstName,
        String lastName,
        Integer weight,
        Integer height,
        String gym,
        TrainingStyle trainingStyle
) {
}
