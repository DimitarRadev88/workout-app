package com.dimitarrradev.userService.app.controller.binding;

public record UserAddModel(
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        Integer weight,
        Integer height,
        String gym
) {

}
