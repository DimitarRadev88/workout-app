package com.dimitarrradev.workoutScheduler.web.binding;

public record ExerciseInWorkoutEditBidingModel(
        Integer count,
        Integer minReps,
        Integer maxReps,
        Double weight,
        Integer rest
) {
}
