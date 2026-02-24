package com.dimitarrradev.exercisesApi.exercise.util;

import com.dimitarrradev.exercisesApi.controller.binding.ExerciseAddModel;
import com.dimitarrradev.exercisesApi.exercise.model.Exercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseFromModelMapper {

    public Exercise fromExerciseAddModel(ExerciseAddModel addModel) {
        return Exercise.builder()
                .name(addModel.name())
                .description(addModel.description())
                .targetBodyPart(addModel.bodyPart())
                .complexity(addModel.complexity())
                .movementType(addModel.movement())
                .build();
    }

}
