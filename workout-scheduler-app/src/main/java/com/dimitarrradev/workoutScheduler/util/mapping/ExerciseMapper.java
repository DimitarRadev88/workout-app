package com.dimitarrradev.workoutScheduler.util.mapping;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("exerciseMapper")
public class ExerciseMapper implements WorkoutSchedulerMapper<Exercise> {

    @Override
    public <E> Exercise mapFrom(E object) {
        if (object.getClass().equals(ExerciseAddBindingModel.class)) {
            return getFromExerciseAddBindingModel((ExerciseAddBindingModel) object);
        }

        throw new IllegalArgumentException("Invalid object type");
    }

    private static Exercise getFromExerciseAddBindingModel(ExerciseAddBindingModel exerciseAddBindingModel) {
        return new Exercise(
                null,
                exerciseAddBindingModel.exerciseName(),
                exerciseAddBindingModel.bodyPart(),
                exerciseAddBindingModel.movementType(),
                exerciseAddBindingModel.description(),
                Collections.emptyList(),
                Boolean.FALSE,
                exerciseAddBindingModel.addedBy(),
                exerciseAddBindingModel.complexity()

        );

    }
}
