package com.dimitarrradev.workoutScheduler.exercise.dto;

import java.util.List;

public record PageAndExerciseReviewServiceView(
        List<ExerciseForReviewViewModel> exercises,
        Long totalElements,
        Integer totalPages,
        String shownElementsRangeAndTotalCountString,
        List<Integer> pageSizes
) {
}
