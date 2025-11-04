package com.dimitarrradev.exercisesApi.exercise.dto;

import com.dimitarrradev.exercisesApi.image.dto.ImageUrlViewModel;

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
