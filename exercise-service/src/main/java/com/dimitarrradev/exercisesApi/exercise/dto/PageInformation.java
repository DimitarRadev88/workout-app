package com.dimitarrradev.exercisesApi.exercise.dto;

import java.util.List;

public record PageInformation(
        String shownElementsRangeAndTotalCountString,
        List<Integer> pageSizes
) {
}
