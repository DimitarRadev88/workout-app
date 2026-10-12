package com.dimitarrradev.userService.app.controller.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record UserPasswordChangeModel(
        @NotBlank
        String email,
        @NotBlank(message = "Password can't be empty!")
        @Length(min = 6, max = 30, message = "Password length must be between 6 and 30 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{6,30}$",
                message = "Password must include at least one small letter, one capital letter, one special symbol and one number!")
        String password,
        @NotBlank
        String token
) {

}
