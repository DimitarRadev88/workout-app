package com.dimitarrradev.workoutScheduler.web.binding;

public record ImageUrlBindingModel(
        Long id,
        String url,
        Boolean forDelete
) {
}
