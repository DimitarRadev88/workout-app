package com.dimitarrradev.workoutScheduler.exercise.dto;

import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;

public record ExerciseForReviewViewModel(
        Long id,
        String name,
        String description,
        Complexity complexity,
        String addedBy
) {
}
