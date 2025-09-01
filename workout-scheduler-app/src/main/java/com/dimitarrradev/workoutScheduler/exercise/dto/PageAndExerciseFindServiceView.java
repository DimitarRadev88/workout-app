package com.dimitarrradev.workoutScheduler.exercise.dto;

import java.util.List;

public record PageAndExerciseFindServiceView(
        List<ExerciseFindViewModel> exercises,
        Long totalElements,
        Integer totalPages,
        String shownElementsRangeAndTotalCountString,
        List<Integer> pageSizes
) {
}


