package com.dimitarrradev.exercisesApi.util.mapping;

import com.dimitarrradev.exercisesApi.exercise.Exercise;
import com.dimitarrradev.exercisesApi.exercise.dto.ExerciseModel;
import com.dimitarrradev.exercisesApi.image.dto.ImageUrlModel;
import org.springframework.stereotype.Component;

@Component
public class ExerciseToViewModelMapper {

    public ExerciseModel toExerciseModel(Exercise exercise) {
        return new ExerciseModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getComplexity(),
                exercise.getDescription(),
                exercise.getMovementType(),
                exercise.getTargetBodyPart(),
                exercise.getImageURLs().stream()
                        .map(imageUrl -> new ImageUrlModel(
                                imageUrl.getId(),
                                imageUrl.getUrl()
                        ))
                        .toList(),
                exercise.getApproved(),
                exercise.getAddedBy()
        );
    }


}
