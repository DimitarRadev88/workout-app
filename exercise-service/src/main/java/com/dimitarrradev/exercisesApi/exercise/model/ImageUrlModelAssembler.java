package com.dimitarrradev.exercisesApi.exercise.model;


import com.dimitarrradev.exercisesApi.web.ExerciseController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ImageUrlModelAssembler implements RepresentationModelAssembler<ImageUrl, ImageUrlModel> {

    @Override
    public ImageUrlModel toModel(ImageUrl imageUrl) {

        ImageUrlModel imageUrlModel = new ImageUrlModel(imageUrl.getId(), imageUrl.getUrl());

        imageUrlModel.add(linkTo(methodOn(ExerciseController.class).getImage(imageUrl.getExercise().getId(), imageUrl.getId())).withRel("self"));
        imageUrlModel.add(linkTo(methodOn(ExerciseController.class).deleteImage(imageUrl.getExercise().getId(), imageUrl.getId())).withRel("delete"));
        imageUrlModel.add(linkTo(methodOn(ExerciseController.class).getExercise(imageUrl.getExercise().getId())).withRel("exercise"));



        return imageUrlModel;
    }

    @Override
    public CollectionModel<ImageUrlModel> toCollectionModel(Iterable<? extends ImageUrl> imageUrls) {
        return RepresentationModelAssembler.super.toCollectionModel(imageUrls);
    }
}
