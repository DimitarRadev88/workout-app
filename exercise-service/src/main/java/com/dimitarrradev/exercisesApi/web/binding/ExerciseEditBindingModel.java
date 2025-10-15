package com.dimitarrradev.exercisesApi.web.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.dimitarrradev.exercisesApi.util.error.message.ErrorMessage.*;

public record ExerciseEditBindingModel(
        Long id,
        @NotBlank(message = EXERCISE_NAME_MESSAGE) @Size(min = 2, max = 100, message = EXERCISE_NAME_MESSAGE) String name,
        @NotBlank @Size(min = 20, message = EXERCISE_DESCRIPTION_MESSAGE)  String description,
        @Pattern(
                regexp = "^https?://[^\\s/$.?#].[^\\s]*(\\r?\\nhttps?://[^\\s/$.?#].[^\\s]*)*$",
                message = IMAGE_URL_MESSAGE
        ) String addImageUrls,
        Boolean approved
) {
}
