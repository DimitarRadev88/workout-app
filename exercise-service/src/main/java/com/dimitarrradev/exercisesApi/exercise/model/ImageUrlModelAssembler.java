package com.dimitarrradev.exercisesApi.exercise.model;


import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlModelAssembler implements RepresentationModelAssembler<ImageUrl, ImageUrlModel> {

    @Override
    public ImageUrlModel toModel(ImageUrl imageUrl) {

        return new ImageUrlModel(imageUrl.getId(), imageUrl.getUrl(), imageUrl.getExercise().getId());
    }

}
