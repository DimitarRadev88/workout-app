package com.dimitarrradev.exercisesApi.util.mapping;

import com.dimitarrradev.exercisesApi.exercise.Exercise;
import com.dimitarrradev.exercisesApi.exercise.dto.*;
import com.dimitarrradev.exercisesApi.image.dto.ImageUrlViewModel;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseEditBindingModel;
import org.springframework.stereotype.Component;

@Component
public class ExerciseToViewModelMapper {

    public ExerciseFindViewModel toExerciseFindViewModel(Exercise exercise) {
        return new ExerciseFindViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getComplexity(),
                exercise.getMovementType()
        );
    }

    public ExerciseViewModel toExerciseViewModel(Exercise exercise) {
        return new ExerciseViewModel(
                exercise.getName(),
                exercise.getComplexity().getName(),
                exercise.getMovementType().getName(),
                exercise.getDescription(),
                exercise.getImageURLs().stream()
                        .map(imageUrl -> new ImageUrlViewModel(
                                imageUrl.getId(),
                                imageUrl.getUrl()
                        ))
                        .toList()
        );
    }

    public ExerciseEditBindingModel toExerciseEditBindingModel(Exercise exercise) {
        return new ExerciseEditBindingModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                null,
                exercise.getApproved()
        );
    }

    public ExerciseNameAndIdViewModel toExerciseNameAndIdViewModel(Exercise exercise) {
        return new ExerciseNameAndIdViewModel(
                exercise.getId(),
                exercise.getName()
        );
    }

    public ExerciseForReviewViewModel toExerciseForReviewViewModel(Exercise exercise) {
        return new ExerciseForReviewViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getComplexity(),
                exercise.getMovementType(),
                exercise.getAddedBy()
        );
    }

}
