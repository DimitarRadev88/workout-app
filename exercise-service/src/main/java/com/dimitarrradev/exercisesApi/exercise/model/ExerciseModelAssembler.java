package com.dimitarrradev.exercisesApi.exercise.model;

import com.dimitarrradev.exercisesApi.web.ExerciseController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

        model.add(linkTo(methodOn(ExerciseController.class).getExercise(model.getId())).withSelfRel());
        model.add(linkTo(methodOn(ExerciseController.class).editExercise(model.getId(), null, null)).withRel("update"));
        model.add(linkTo(methodOn(ExerciseController.class).deleteExercise(model.getId())).withRel("delete"));
        model.add(linkTo(methodOn(ExerciseController.class).getImages(model.getId())).withRel("images"));

        return model;
    }

    @Override
    public CollectionModel<ExerciseModel> toCollectionModel(Iterable<? extends Exercise> exercises) {
        return RepresentationModelAssembler.super.toCollectionModel(exercises);
    }
}
