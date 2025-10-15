package com.dimitarrradev.exercisesApi.exercise.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MovementType {
    COMPOUND("Compound"),
    ISOLATION("Isolation"),
    ALL("All");

    private final String name;

}
