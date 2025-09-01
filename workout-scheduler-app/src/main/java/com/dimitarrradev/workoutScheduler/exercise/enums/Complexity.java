package com.dimitarrradev.workoutScheduler.exercise.enums;

public enum Complexity {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    ALL("All");

    private final String name;

    Complexity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
