package com.dimitarrradev.exercisesApi.exercise.util;

import com.dimitarrradev.exercisesApi.exercise.model.Exercise;
import com.dimitarrradev.exercisesApi.exercise.model.ExerciseModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ExerciseModelAssembler implements RepresentationModelAssembler<Exercise, ExerciseModel> {

    @Override
    public ExerciseModel toModel(Exercise entity) {
        ExerciseModel model = new ExerciseModel(
                entity.getId(),
                entity.getName(),
                entity.getComplexity(),
                entity.getDescription(),
                entity.getMovementType(),
                entity.getTargetBodyPart()
        );

        return model;
    }

}
