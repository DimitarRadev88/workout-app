package com.dimitarrradev.workoutScheduler.exercise.dto.view;

import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;

public record ExerciseForReviewViewModel(
        Long id,
        String name,
        String description,
        Complexity complexity,
        MovementType movementType,
        String addedBy
) {
}
