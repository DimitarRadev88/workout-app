package com.dimitarrradev.workoutScheduler.util.mapping;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.dto.*;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseEditBindingModel;
import org.springframework.stereotype.Component;

@Component
public class ExerciseToViewModelMapper {

    public ExerciseFindViewModel toExerciseFindViewModel(Exercise exercise) {
        return new ExerciseFindViewModel(
                exercise.id(),
                exercise.name(),
                exercise.complexity(),
                exercise.movementType()
        );
    }

    public ExerciseViewModel toExerciseViewModel(Exercise exercise) {
        return new ExerciseViewModel(
                exercise.name(),
                exercise.complexity().getName(),
                exercise.movementType().getName(),
                exercise.description(),
                exercise.imageURLs().stream()
                        .map(imageUrl -> new ImageUrlViewModel(
                                imageUrl.getId(),
                                imageUrl.getUrl()
                        ))
                        .toList()
        );
    }

    public ExerciseEditBindingModel toExerciseEditBindingModel(Exercise exercise) {
        return new ExerciseEditBindingModel(
                exercise.id(),
                exercise.name(),
                exercise.description(),
                null,
                exercise.approved()
        );
    }

    public ExerciseNameAndIdViewModel toExerciseNameAndIdViewModel(Exercise exercise) {
        return new ExerciseNameAndIdViewModel(
                exercise.id(),
                exercise.name()
        );
    }

    public ExerciseForReviewViewModel toExerciseForReviewViewModel(Exercise exercise) {
        return new ExerciseForReviewViewModel(
                exercise.id(),
                exercise.name(),
                exercise.description(),
                exercise.complexity(),
                exercise.movementType(),
                exercise.addedBy()
        );
    }

}
