package com.dimitarrradev.workoutScheduler.exercise.dto;

import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;

public record ExerciseFindViewModel(
        Long id,
        String name,
        Complexity complexity
) {
}
