package com.dimitarrradev.workoutScheduler.util.mapping;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import org.springframework.stereotype.Component;

@Component("exerciseMapper")
public class ExerciseMapper implements WorkoutSchedulerMapper<Exercise> {

    @Override
    public <E> Exercise mapFrom(E object) {
        if (object.getClass().equals(ExerciseAddBindingModel.class)) {
            return getFromExerciseAddBindingModel((ExerciseAddBindingModel) object);
        }

        throw new IllegalArgumentException("Invalid object type");
    }

    private static <E> Exercise getFromExerciseAddBindingModel(ExerciseAddBindingModel exerciseAddBindingModel) {
        Exercise exercise = new Exercise();
        exercise.setName(exerciseAddBindingModel.exerciseName());
        exercise.setDescription(exerciseAddBindingModel.description());
        exercise.setApproved(false);
        exercise.setAddedBy(exerciseAddBindingModel.addedBy());
        exercise.setComplexity(exerciseAddBindingModel.complexity());
        exercise.setMovementType(exerciseAddBindingModel.movementType());
        exercise.setTargetBodyPart(exerciseAddBindingModel.bodyPart());
        return exercise;
    }
}
