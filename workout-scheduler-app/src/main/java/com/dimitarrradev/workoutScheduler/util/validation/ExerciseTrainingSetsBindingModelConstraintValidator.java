package com.dimitarrradev.workoutScheduler.util.validation;

import com.dimitarrradev.workoutScheduler.web.binding.TrainingSetBindingModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExerciseTrainingSetsBindingModelConstraintValidator implements ConstraintValidator<TrainingSetConstraint, TrainingSetBindingModel> {
    @Override
    public boolean isValid(TrainingSetBindingModel trainingSetBindingModel, ConstraintValidatorContext constraintValidatorContext) {
        return trainingSetBindingModel != null &&
                trainingSetBindingModel.count() != null && trainingSetBindingModel.count() > 0 &&
                trainingSetBindingModel.minReps() != null && trainingSetBindingModel.minReps() > 0 &&
                trainingSetBindingModel.maxReps() != null && trainingSetBindingModel.maxReps() > 0 &&
                trainingSetBindingModel.minReps() <= trainingSetBindingModel.maxReps() &&
                trainingSetBindingModel.weight() != null && trainingSetBindingModel.weight() > 0 &&
                trainingSetBindingModel.rest() != null && trainingSetBindingModel.rest() > 0;
    }
}
