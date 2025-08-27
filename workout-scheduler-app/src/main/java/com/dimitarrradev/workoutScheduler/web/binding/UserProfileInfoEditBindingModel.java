package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.exercise.enums.TrainingStyle;
import jakarta.validation.constraints.*;

public record UserProfileInfoEditBindingModel(
        @PositiveOrZero(message = "Weight cannot be negative") Integer weight,
        @PositiveOrZero(message = "Height cannot be negative") Integer height,
        @Size(max = 30, message = "Gym length cannot be more than 30 symbols") String gym,
        @NotNull(message = "You must select training style from the menu") TrainingStyle trainingStyle
) {
}
