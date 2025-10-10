package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.util.validation.WorkoutExerciseConstraint;
import jakarta.validation.constraints.NotNull;

public record ExerciseWorkoutExerciseBindingModel(
    @NotNull(message = "Please choose an exercise") Long exerciseId,
    @NotNull(message = "Please enter exercise information") @WorkoutExerciseConstraint WorkoutExerciseBindingModel workoutExerciseBindingModel
) {

}
