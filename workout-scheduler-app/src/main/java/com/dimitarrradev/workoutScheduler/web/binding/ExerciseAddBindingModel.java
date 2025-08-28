package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;

public record ExerciseAddBindingModel(
        String exerciseName,
        String equipment,
        String description,
        TargetBodyPart bodyPart
) {
}
