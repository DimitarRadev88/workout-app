package com.dimitarrradev.workoutScheduler.web.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileAccountEditBindingModel(
        String username,
        String email,
        @Size(max = 30, message = "First Name length cannot be more than 30 symbols") String firstName,
        @Size(max = 30, message = "Last Name length cannot be more than 30 symbols") String lastName,
        Boolean isChangingPassword
) {
}
