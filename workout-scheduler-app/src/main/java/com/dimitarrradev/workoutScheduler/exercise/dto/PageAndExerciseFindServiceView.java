package com.dimitarrradev.workoutScheduler.exercise.dto;

import java.util.List;

public record PageAndExerciseFindServiceView(
        List<ExerciseFindViewModel> content,
        long totalElements,
        int totalPages,
        String format,
        List<Integer> integers) {
}


