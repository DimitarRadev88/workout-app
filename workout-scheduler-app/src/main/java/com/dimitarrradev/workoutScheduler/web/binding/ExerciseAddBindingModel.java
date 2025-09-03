package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExerciseAddBindingModel(
        @Size(min = 2, max = 100, message = "Exercise name should be between 2 and 100 symbols") String exerciseName,
        @Size(min = 20, message = "Please provide simple exercise execution guide") String description,
        @NotNull(message = "You must select the main muscle group involved in exercise execution") TargetBodyPart bodyPart,
        String addedBy,
        @NotNull(message = "You must select exercise Complexity (How hard is for execution)") Complexity complexity
) {
}
