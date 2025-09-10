package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseNameAndIdViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseViewModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;

import java.util.List;

public record WorkoutAddBindingModel(
        WorkoutType workoutType,
        List<TargetBodyPart> targetBodyParts,
        List<TrainingSet> trainingSets
) {
}
