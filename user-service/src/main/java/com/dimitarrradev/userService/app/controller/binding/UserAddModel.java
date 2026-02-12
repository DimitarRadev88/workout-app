package com.dimitarrradev.userService.app.controller.binding;

public record UserAddModel(
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        Double weight,
        Double height,
        String gym
) {

}
