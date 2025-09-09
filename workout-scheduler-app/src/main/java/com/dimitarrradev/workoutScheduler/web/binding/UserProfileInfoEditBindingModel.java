package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UserProfileInfoEditBindingModel(
        @PositiveOrZero(message = "Weight cannot be negative") Integer weight,
        @PositiveOrZero(message = "Height cannot be negative") Integer height,
        @Size(max = 30, message = "Gym length cannot be more than 30 symbols") String gym,
        @NotNull(message = "You must select workout type from the menu") WorkoutType workoutType
) {
}
