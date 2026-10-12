package com.dimitarrradev.workoutScheduler.user.dto;

public record UserProfileAccountViewModel(
        String username,
        String email,
        String firstName,
        String lastName
) {
}
