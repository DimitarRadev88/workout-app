package com.dimitarrradev.exercisesApi.exercise.dto;

import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;

public record ExerciseFindViewModel(
        Long id,
        String name,
        Complexity complexity,
        MovementType movementType
) {
}
