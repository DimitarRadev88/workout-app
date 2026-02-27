package com.dimitarrradev.exercisesApi.controller.binding;

import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExerciseEditModel (
    @NotBlank String name,
    @NotBlank String description,
    @NotNull TargetBodyPart bodyPart,
    @NotNull Complexity complexity,
    @NotNull MovementType movement
) {
}
