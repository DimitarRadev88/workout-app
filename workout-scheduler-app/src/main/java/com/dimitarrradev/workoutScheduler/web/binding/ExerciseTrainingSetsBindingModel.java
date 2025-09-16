package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.util.validation.TrainingSetConstraint;
import jakarta.validation.constraints.NotNull;

public record ExerciseTrainingSetsBindingModel(
    @NotNull(message = "Please choose an exercise") Long exerciseId,
    @NotNull(message = "Please enter training set information") @TrainingSetConstraint TrainingSetBindingModel trainingSet
) {

}
