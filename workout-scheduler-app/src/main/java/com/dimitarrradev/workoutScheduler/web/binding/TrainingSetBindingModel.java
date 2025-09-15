package com.dimitarrradev.workoutScheduler.web.binding;

public record TrainingSetBindingModel(
        Integer sets,
        Integer minReps,
        Integer maxReps,
        Integer rest,
        Integer weight
) {
}
