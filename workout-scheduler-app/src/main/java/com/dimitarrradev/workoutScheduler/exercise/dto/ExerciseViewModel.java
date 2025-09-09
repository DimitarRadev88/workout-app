package com.dimitarrradev.workoutScheduler.exercise.dto;

import java.util.List;

public record ExerciseViewModel(
        String name,
        String complexity,
        String movementType,
        String description,
        List<ImageUrlViewModel> imageUrls
) {
}
