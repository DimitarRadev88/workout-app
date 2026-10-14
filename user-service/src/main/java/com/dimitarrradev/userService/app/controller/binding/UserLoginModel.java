package com.dimitarrradev.userService.app.controller.binding;

import jakarta.validation.constraints.NotBlank;

public record UserLoginModel(
        @NotBlank
        String username,
        @NotBlank
        String password
) {

}
