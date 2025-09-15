package com.dimitarrradev.workoutScheduler.web.binding;

public record ExerciseTrainingSetsBindingModel(
    Long exerciseId,
    TrainingSetBindingModel trainingSet
) {

}
