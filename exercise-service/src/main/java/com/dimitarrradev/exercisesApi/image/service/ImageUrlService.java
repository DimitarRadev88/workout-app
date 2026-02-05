package com.dimitarrradev.exercisesApi.image.service;

import com.dimitarrradev.exercisesApi.image.dao.ImageUrlRepository;
import com.dimitarrradev.exercisesApi.image.dto.ImageUrlModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageUrlService {

    private final ImageUrlRepository imageUrlRepository;

    public void deleteImageUrl(long id) {
        imageUrlRepository.deleteById(id);
    }

    public List<ImageUrlModel> getExerciseImages(long exerciseId) {
        List<ImageUrlModel> result = imageUrlRepository.findByExercise_Id(exerciseId).stream()
                .map(imageUrl -> new ImageUrlModel(imageUrl.getId(), imageUrl.getUrl()))
                .toList();

        return result.isEmpty() ? new ArrayList<>() : result;

    }
}
