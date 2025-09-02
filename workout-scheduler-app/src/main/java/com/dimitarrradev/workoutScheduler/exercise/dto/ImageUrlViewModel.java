package com.dimitarrradev.workoutScheduler.exercise.dto;

public record ImageUrlViewModel(
        Long id,
        String url,
        Boolean forDelete
) {
}
