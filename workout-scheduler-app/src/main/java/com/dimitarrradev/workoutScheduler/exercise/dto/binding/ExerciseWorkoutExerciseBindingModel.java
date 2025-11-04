package com.dimitarrradev.workoutScheduler.exercise.dto.binding;

import com.dimitarrradev.workoutScheduler.util.validation.WorkoutExerciseConstraint;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutExerciseBindingModel;
import jakarta.validation.constraints.NotNull;

public record ExerciseWorkoutExerciseBindingModel(
    @NotNull(message = "Please choose an exercise") Long exerciseId,
    @NotNull(message = "Please enter exercise information") @WorkoutExerciseConstraint WorkoutExerciseBindingModel workoutExerciseBindingModel
) {

}
