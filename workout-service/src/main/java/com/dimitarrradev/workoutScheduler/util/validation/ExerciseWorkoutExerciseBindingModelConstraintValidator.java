package com.dimitarrradev.workoutScheduler.util.validation;

import com.dimitarrradev.workoutScheduler.web.binding.WorkoutExerciseBindingModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExerciseWorkoutExerciseBindingModelConstraintValidator implements ConstraintValidator<WorkoutExerciseConstraint, WorkoutExerciseBindingModel> {
    @Override
    public boolean isValid(WorkoutExerciseBindingModel workoutExerciseBindingModel, ConstraintValidatorContext constraintValidatorContext) {
        return workoutExerciseBindingModel != null &&
                workoutExerciseBindingModel.sets() != null && workoutExerciseBindingModel.sets() > 0 &&
                workoutExerciseBindingModel.minReps() != null && workoutExerciseBindingModel.minReps() > 0 &&
                workoutExerciseBindingModel.maxReps() != null && workoutExerciseBindingModel.maxReps() > 0 &&
                workoutExerciseBindingModel.minReps() <= workoutExerciseBindingModel.maxReps() &&
                workoutExerciseBindingModel.weight() != null && workoutExerciseBindingModel.weight() > 0 &&
                workoutExerciseBindingModel.rest() != null && workoutExerciseBindingModel.rest() > 0;
    }
}
