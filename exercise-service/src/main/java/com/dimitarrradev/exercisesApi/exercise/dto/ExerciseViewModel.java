package com.dimitarrradev.exercisesApi.exercise.dto;

import com.dimitarrradev.exercisesApi.image.dto.ImageUrlViewModel;

import java.util.List;

public record ExerciseViewModel(
        String name,
        String complexity,
        String movementType,
        String description,
        List<ImageUrlViewModel> imageUrls
) {
}
