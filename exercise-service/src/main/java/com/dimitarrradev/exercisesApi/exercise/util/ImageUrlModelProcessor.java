package com.dimitarrradev.exercisesApi.exercise.util;

import com.dimitarrradev.exercisesApi.exercise.model.ImageUrlModel;
import com.dimitarrradev.exercisesApi.controller.ExerciseController;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ImageUrlModelProcessor implements RepresentationModelProcessor<ImageUrlModel> {

    @Override
    public ImageUrlModel process(ImageUrlModel model) {
        model.add(linkTo(methodOn(ExerciseController.class).getImage(model.getExerciseId(), model.getId())).withRel("self"));
        model.add(linkTo(methodOn(ExerciseController.class).deleteImage(model.getExerciseId(), model.getId())).withRel("delete"));
        model.add(linkTo(methodOn(ExerciseController.class).getExercise(model.getExerciseId())).withRel("exercise"));

        return model;
    }

}
