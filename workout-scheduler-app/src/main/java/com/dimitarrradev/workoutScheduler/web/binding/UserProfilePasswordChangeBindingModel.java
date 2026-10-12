package com.dimitarrradev.workoutScheduler.web.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfilePasswordChangeBindingModel(
    @NotBlank(message = "You must enter your old password in order to change it") String oldPassword,
    @Size(min = 6, max = 30, message = "Password length must be between 6 and 30 symbols") String newPassword,
    @NotBlank(message = "You must repeat your new password for confirmation") String confirmPassword
) {}

//TODO Add password matcher
