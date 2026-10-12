package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import static com.dimitarrradev.workoutScheduler.errors.ErrorMessage.TARGET_BODY_PARTS_ERROR_MESSAGE;
import static com.dimitarrradev.workoutScheduler.errors.ErrorMessage.WORKOUT_TYPE_ERROR_MESSAGE;

public record WorkoutEditBindingModel(
        @NotNull(message = WORKOUT_TYPE_ERROR_MESSAGE) WorkoutType workoutType,
        @NotNull(message = TARGET_BODY_PARTS_ERROR_MESSAGE)
        @NotEmpty(message = TARGET_BODY_PARTS_ERROR_MESSAGE) List<TargetBodyPart> targetBodyParts
) {
}
