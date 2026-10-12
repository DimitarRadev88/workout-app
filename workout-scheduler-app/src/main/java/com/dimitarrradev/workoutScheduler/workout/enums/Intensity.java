package com.dimitarrradev.workoutScheduler.workout.enums;

public enum Intensity {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String name;

    Intensity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
