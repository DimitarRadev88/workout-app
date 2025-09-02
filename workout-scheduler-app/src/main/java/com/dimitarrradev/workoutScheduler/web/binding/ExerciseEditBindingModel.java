package com.dimitarrradev.workoutScheduler.web.binding;

import java.util.List;

public record ExerciseEditBindingModel(
        Long id,
        String name,
        String description,
        String imageUrl,
        List<ImageUrlBindingModel> imageUrls
) {
}
