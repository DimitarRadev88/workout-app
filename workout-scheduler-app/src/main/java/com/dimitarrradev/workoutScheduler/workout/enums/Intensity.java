package com.dimitarrradev.workoutScheduler.workout.enums;

public enum Intensity {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String value;

    Intensity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
