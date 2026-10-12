package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import static com.dimitarrradev.workoutScheduler.errors.ErrorMessage.*;

public record WorkoutAddBindingModel(
        @NotNull(message = WORKOUT_TYPE_ERROR_MESSAGE) WorkoutType workoutType,
        @NotNull(message = TARGET_BODY_PARTS_ERROR_MESSAGE)
        @NotEmpty(message = TARGET_BODY_PARTS_ERROR_MESSAGE) List<TargetBodyPart> targetBodyParts,
        @NotNull(message = WORKOUT_DATE_ERROR_MESSAGE) LocalDateTime workoutDateTime
) {
}
