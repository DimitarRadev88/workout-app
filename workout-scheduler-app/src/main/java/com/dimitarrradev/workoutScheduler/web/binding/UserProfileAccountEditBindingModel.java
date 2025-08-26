package com.dimitarrradev.workoutScheduler.web.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileAccountEditBindingModel(
        String username,
        String email,
        @Size(max = 30, message = "First Name length cannot be more than 30 symbols") String firstName,
        @Size(max = 30, message = "Last Name length cannot be more than 30 symbols") String lastName,
        Boolean isChangingPassword,
        @NotBlank(message = "You must enter your old password if you want to replace it") String  oldPassword,
        @Size(min = 6, max = 30, message = "New password length must be between 6 and 30 symbols") String newPassword
) {
}
