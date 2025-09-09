package com.dimitarrradev.workoutScheduler.exercise.enums;

public enum MovementType {
    COMPOUND("Compound"),
    ISOLATION("Isolation"),
    All("All");

    private final String name;
    MovementType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
