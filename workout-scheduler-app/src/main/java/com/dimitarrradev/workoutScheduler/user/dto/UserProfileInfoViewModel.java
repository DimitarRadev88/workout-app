package com.dimitarrradev.workoutScheduler.user.dto;


import com.dimitarrradev.workoutScheduler.training.TrainingStyle;

public record UserProfileInfoViewModel(
        Integer weight,
        Integer height,
        String gym,
        TrainingStyle trainingStyle
) {

}
