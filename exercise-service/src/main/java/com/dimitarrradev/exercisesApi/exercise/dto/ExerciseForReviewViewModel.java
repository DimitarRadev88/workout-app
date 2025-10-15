package com.dimitarrradev.exercisesApi.exercise.dto;

import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;

public record ExerciseForReviewViewModel(
        Long id,
        String name,
        String description,
        Complexity complexity,
        MovementType movementType,
        String addedBy
) {
}
