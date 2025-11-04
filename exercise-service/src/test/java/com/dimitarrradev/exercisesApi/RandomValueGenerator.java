package com.dimitarrradev.exercisesApi;

import com.dimitarrradev.exercisesApi.exercise.Exercise;
import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomValueGenerator {

    public static long randomId() {
        return ThreadLocalRandom.current().nextLong(0L, Long.MAX_VALUE);
    }

    public static String randomExerciseName() {
        return "Exercise " + randomTargetBodyPart() + " " + randomMovementType();
    }

    public static Complexity randomComplexity() {
        return Complexity.values()[ThreadLocalRandom.current().nextInt(0, Complexity.values().length - 1)];
    }

    public static boolean randomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static MovementType randomMovementType() {
        return MovementType.values()[ThreadLocalRandom.current().nextInt(0, MovementType.values().length - 1)];
    }

    public static TargetBodyPart randomTargetBodyPart() {
        return TargetBodyPart.values()[ThreadLocalRandom.current().nextInt(0, TargetBodyPart.values().length - 1)];
    }

    public static String randomDescription() {
        return "Description" + randomTargetBodyPart() + randomMovementType() + randomComplexity();
    }

    public static List<TargetBodyPart> randomTargetBodyPartsList() {
        int count = ThreadLocalRandom.current().nextInt(1,TargetBodyPart.values().length - 1);

        List<TargetBodyPart> targetBodyPartNames = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(TargetBodyPart.values().length - 1);
            TargetBodyPart targetBodyPart = TargetBodyPart.values()[randomIndex];
            if (targetBodyPartNames.contains(targetBodyPart)) {
                i--;
            } else {
                targetBodyPartNames.add(targetBodyPart);
            }
        }

        return targetBodyPartNames;
    }

    public static List<Exercise> randomExerciseList(int count) {
        List<Exercise> exercises = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Exercise e = new Exercise(
                    RandomValueGenerator.randomId(),
                    randomExerciseName(),
                    randomTargetBodyPart(),
                    randomMovementType(),
                    randomDescription(),
                    Collections.emptyList(),
                    randomBoolean(),
                    "user" + i,
                    randomComplexity()
            );

            exercises.add(e);
        }

        return exercises;
    }

}
