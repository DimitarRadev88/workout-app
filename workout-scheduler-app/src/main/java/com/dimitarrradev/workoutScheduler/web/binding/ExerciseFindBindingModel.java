package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;

public record ExerciseFindBindingModel (
        TargetBodyPart targetBodyPart,
        Complexity complexity
) {
}
