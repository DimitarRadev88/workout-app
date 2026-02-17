package com.dimitarrradev.userService.app.controller.binding;

import jakarta.validation.constraints.Positive;

public record UserEditModel(
        String firstName,
        String lastName,
        @Positive(message = "Weight must be positive")
        Double weight,
        @Positive(message = "Height must be positive")
        Double height,
        String gym
) {
}
