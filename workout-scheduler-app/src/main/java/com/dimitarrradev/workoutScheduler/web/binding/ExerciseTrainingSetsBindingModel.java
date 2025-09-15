package com.dimitarrradev.workoutScheduler.web.binding;

public record ExerciseTrainingSetsBindingModel(
    ExerciseNameAndIdBindingModel exerciseNameAndId,
    TrainingSetBindingModel trainingSet
) {

}
