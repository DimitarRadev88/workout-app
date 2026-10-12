package com.dimitarrradev.workoutScheduler.exercise.dto.view;

import java.util.List;

public record ExerciseViewModel(
        Long id,
        String name,
        String complexity,
        String movementType,
        String description,
        String addedBy,
        Boolean approved,
        List<ImageUrlViewModel> imageUrls
) {
}
