package com.dimitarrradev.exercisesApi.exercise.util;

import com.dimitarrradev.exercisesApi.controller.binding.ImageUrlsAddModel;
import com.dimitarrradev.exercisesApi.exercise.model.ImageUrl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImageUrlFromModelMapper {

    public List<ImageUrl> fromImageUrlsAddModel(ImageUrlsAddModel addModel) {
        return addModel
                .urls()
                .stream()
                .map(url -> new ImageUrl(null, url, null, false))
                .toList();
    }
}
