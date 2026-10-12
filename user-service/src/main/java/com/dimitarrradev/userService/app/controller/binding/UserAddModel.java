package com.dimitarrradev.userService.app.controller.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record UserAddModel(
        @NotBlank(message = "Username can't be empty!")
        @Length(min = 2, max = 30, message = "Username length must be between 2 and 30 characters!")
        String username,
        String firstName,
        String lastName,
        @NotBlank(message = "Email can't be empty!")
        @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
        String email,
        @NotBlank(message = "Password can't be empty!")
        @Length(min = 6, max = 30, message = "Password length must be between 6 and 30 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{6,30}$",
                message = "Password must include at least one small letter, one capital letter, one special symbol and one number!")
        String password,
        @Positive(message = "Weight must be positive")
        Double weight,
        @Positive(message = "Height must be positive")
        Double height,
        String gym
) {

}
