package com.dimitarrradev.exercisesApi.util.mapping;

import com.dimitarrradev.exercisesApi.exercise.Exercise;
import com.dimitarrradev.exercisesApi.image.ImageUrl;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseEditBindingModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ExerciseFromBindingModelMapper {

    public Exercise fromExerciseAddBindingModel(ExerciseAddBindingModel exerciseAddBindingModel) {
        return new Exercise(
                null,
                exerciseAddBindingModel.exerciseName(),
                exerciseAddBindingModel.bodyPart(),
                exerciseAddBindingModel.movementType(),
                exerciseAddBindingModel.description(),
                Collections.emptyList(),
                Boolean.FALSE,
                exerciseAddBindingModel.addedBy(),
                exerciseAddBindingModel.complexity()
        );
    }

    public Exercise fromExerciseEditBindingModel(Exercise exercise, ExerciseEditBindingModel exerciseEdit) {
        List<ImageUrl> list = new ArrayList<>(exercise.getImageURLs());

        if (exerciseEdit.addImageUrls() != null && !exerciseEdit.addImageUrls().isBlank()) {
            Arrays
                    .stream(exerciseEdit.addImageUrls().split(System.lineSeparator()))
                    .map(url -> {
                        ImageUrl imageUrl = new ImageUrl();
                        imageUrl.setUrl(url.trim());
                        imageUrl.setExercise(exercise);
                        return imageUrl;
                    }).forEach(list::add);
        }

        return new Exercise(
                exercise.getId(),
                exerciseEdit.name(),
                exercise.getTargetBodyPart(),
                exercise.getMovementType(),
                exerciseEdit.description(),
                list,
                exerciseEdit.approved(),
                exercise.getAddedBy(),
                exercise.getComplexity()
        );
    }

}
