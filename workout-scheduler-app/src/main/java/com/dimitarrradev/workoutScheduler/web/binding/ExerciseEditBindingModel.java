package com.dimitarrradev.workoutScheduler.web.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ExerciseEditBindingModel(
        Long id,
        @NotBlank @Size(min = 2, max = 100, message = "Exercise name should be between 2 and 100 symbols") String name,
        @NotBlank @Size(min = 20, message = "Please provide simple exercise execution guide")  String description,
        @Pattern(
                regexp = "^https?://[^\\s/$.?#].[^\\s]*(\\r?\\nhttps?://[^\\s/$.?#].[^\\s]*)*$",
                message = "The provided url is not valid"
        ) String addImageUrls,
        Boolean isApproved
) {
}
