package com.dimitarrradev.demo.controller.model;

public record UserRegisterBindingModel(
        String username,
        String email,
        String password,
        String confirmPassword
) {
}
