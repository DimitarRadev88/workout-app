package com.dimitarrradev.workoutScheduler.web.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static com.dimitarrradev.workoutScheduler.exception.ErrorMessage.LOGIN_ERROR_MESSAGE;

public record UserLoginBindingModel(
        String username,
        String password
) {
}

