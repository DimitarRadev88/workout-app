package com.dimitarrradev.userService.app.controller.binding;

public record UserEditModel(
        String firstName,
        String lastName,
        Double weight,
        Double height,
        String gym
) {
}
