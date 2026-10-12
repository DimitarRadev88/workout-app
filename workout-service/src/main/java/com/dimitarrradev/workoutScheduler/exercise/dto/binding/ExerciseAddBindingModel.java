package com.dimitarrradev.workoutScheduler.exercise.dto.binding;

import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static com.dimitarrradev.workoutScheduler.errors.ErrorMessage.*;

public record ExerciseAddBindingModel(
        @NotBlank(message = EXERCISE_NAME_MESSAGE) @Size(min = 2, max = 100, message = EXERCISE_NAME_MESSAGE) String exerciseName,
        @NotBlank(message = EXERCISE_DESCRIPTION_MESSAGE) @Size(min = 20, message = EXERCISE_DESCRIPTION_MESSAGE) String description,
        @NotNull(message = TARGET_BODY_PART_MESSAGE) TargetBodyPart bodyPart,
        String addedBy,
        @NotNull(message = EXERCISE_COMPLEXITY_MESSAGE) Complexity complexity,
        @NotNull(message = EXERCISE_COMPLEXITY_MESSAGE) MovementType movementType
) {
}
