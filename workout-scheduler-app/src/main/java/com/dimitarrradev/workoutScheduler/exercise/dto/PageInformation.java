package com.dimitarrradev.workoutScheduler.exercise.dto;

import java.util.List;

public record PageInformation(
        String shownElementsRangeAndTotalCountString,
        List<Integer> pageSizes
) {
}
