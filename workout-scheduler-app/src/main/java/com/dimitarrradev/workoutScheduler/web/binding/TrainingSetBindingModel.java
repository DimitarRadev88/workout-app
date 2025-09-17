package com.dimitarrradev.workoutScheduler.web.binding;

public record TrainingSetBindingModel(
        Integer count,
        Integer minReps,
        Integer maxReps,
        Integer rest,
        Double weight
) {
}
