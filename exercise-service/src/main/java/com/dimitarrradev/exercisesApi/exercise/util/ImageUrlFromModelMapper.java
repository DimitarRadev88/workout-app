package com.dimitarrradev.exercisesApi.exercise.util;

import com.dimitarrradev.exercisesApi.controller.binding.ImageUrlAddModel;
import com.dimitarrradev.exercisesApi.exercise.model.ImageUrl;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlFromModelMapper {

    public ImageUrl fromImageUrlAddModel(ImageUrlAddModel addModel) {
        return new ImageUrl(null, addModel.url(), null);
    }

}
