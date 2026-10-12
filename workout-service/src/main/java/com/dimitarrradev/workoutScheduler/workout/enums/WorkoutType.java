package com.dimitarrradev.workoutScheduler.workout.enums;

public enum WorkoutType {
    CARDIO("Cardio"),
    FITNESS("Fitness"),
    WEIGHTLIFTING("Weightlifting"),
    BODYBUILDING("Bodybuilding"),
    CROSSFIT("Crossfit"),
    CALISTHENICS("Calisthenics");

    private final String name;

    WorkoutType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
