package com.dimitarrradev.workoutScheduler.web.binding;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserRegisterBindingModel(
        @Size(min = 2, max = 30, message = "Username length must be between 2 and 30 symbols") String username,
        @Email String email,
        @Size(min = 6, max = 30, message = "Password length must be between 6 and 30 symbols") String password,
        @Size(min = 6, max = 30, message = "Password length must be between 6 and 30 symbols") String confirmPassword
) {
}
