package com.dimitarrradev.workoutScheduler.exercise.enums;

public enum MovementType {
    COMPOUND("Compound"),
    ISOLATION("Isolation"),
    ALL("All");

    private final String name;
    MovementType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
