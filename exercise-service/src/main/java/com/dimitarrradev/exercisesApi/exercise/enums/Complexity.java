package com.dimitarrradev.exercisesApi.exercise.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Complexity {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    ALL("All");

    private final String name;

}
