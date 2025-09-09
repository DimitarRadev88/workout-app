package com.dimitarrradev.workoutScheduler.exercise.enums;

public enum TargetBodyPart {
    CHEST("Chest"),
    BACK("Back"),
    SHOULDERS("Shoulders"),
    TRAPS("Traps"),
    TRICEPS("Triceps"),
    BICEPS("Biceps"),
    QUADS("Quads"),
    HAMSTRINGS("Hamstrings"),
    GLUTES("Glutes"),
    SPINAL_ERECTORS("Spinal Erectors"),
    ABS("Abs"),
    OBLIQUES("Obliques"),
    CALVES("Calves"),
    FOREARMS("Forearms"),
    LEGS("Legs"),
    ADDUCTORS("Adductors"),
    ABDUCTORS("Abductors"),
    ALL("All");

    private final String name;

    TargetBodyPart(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return TargetBodyPart.values().length;
    }

}
