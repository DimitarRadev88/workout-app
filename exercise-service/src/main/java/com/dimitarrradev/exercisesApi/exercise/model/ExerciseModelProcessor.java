package com.dimitarrradev.exercisesApi.exercise.model;

import com.dimitarrradev.exercisesApi.web.ExerciseController;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ExerciseModelProcessor implements RepresentationModelProcessor<ExerciseModel> {

    @Override
    public ExerciseModel process(ExerciseModel model) {
        model.add(linkTo(methodOn(ExerciseController.class).getExercise(model.getId())).withSelfRel());
        model.add(linkTo(methodOn(ExerciseController.class).editExercise(model.getId(), null, null)).withRel("update"));
        model.add(linkTo(methodOn(ExerciseController.class).deleteExercise(model.getId())).withRel("delete"));
        model.add(linkTo(methodOn(ExerciseController.class).getImages(model.getId())).withRel("images"));

        return model;
    }

}
