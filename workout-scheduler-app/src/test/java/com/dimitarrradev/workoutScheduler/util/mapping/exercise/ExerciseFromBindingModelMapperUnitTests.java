package com.dimitarrradev.workoutScheduler.util.mapping.exercise;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.ImageUrl;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.util.mapping.exercise.ExerciseFromBindingModelMapper;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseEditBindingModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.dimitarrradev.workoutScheduler.RandomValueGenerator.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class ExerciseFromBindingModelMapperUnitTests {

    @InjectMocks
    private ExerciseFromBindingModelMapper exerciseFrom;

    @Test
    void testFromExerciseAddBindingModelReturnsCorrectExercise() {
        ExerciseAddBindingModel exerciseAddBindingModel = new ExerciseAddBindingModel(
                randomExerciseName(),
                randomDescription(),
                randomTargetBodyPart(),
                "user",
                randomComplexity(),
                randomMovementType()
        );

        Exercise expected = new Exercise(
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

        Exercise exercise = exerciseFrom.fromExerciseAddBindingModel(exerciseAddBindingModel);

        assertThat(exercise).isEqualTo(expected);
    }

    @Test
    void testFromExerciseEditBindingModelReturnsCorrectExerciseWithNewImageUrls() {
        Exercise exercise = new Exercise(
                1L,
                randomExerciseName(),
                randomTargetBodyPart(),
                randomMovementType(),
                randomDescription(),
                new ArrayList<>(List.of(new ImageUrl(1L, "existing-url", null))),
                Boolean.FALSE,
                "user",
                randomComplexity()
        );

        ExerciseEditBindingModel exerciseEditBindingModel = new ExerciseEditBindingModel(
                1L,
                randomExerciseName(),
                randomDescription(),
                String.format("%s%n%s", "imageUrl", "imageUrl2"),
                Boolean.TRUE
        );

        List<ImageUrl> imageUrls = new ArrayList<>(exercise.getImageURLs());

        Arrays
                .stream(exerciseEditBindingModel
                        .addImageUrls()
                        .split(System.lineSeparator()))
                .map(url -> new ImageUrl(null, url, exercise))
                .forEach(imageUrls::add);


        Exercise expected = new Exercise(
                exercise.getId(),
                exerciseEditBindingModel.name(),
                exercise.getTargetBodyPart(),
                exercise.getMovementType(),
                exerciseEditBindingModel.description(),
                imageUrls,
                exerciseEditBindingModel.approved(),
                exercise.getAddedBy(),
                exercise.getComplexity()
        );

        assertThat(exerciseFrom.fromExerciseEditBindingModel(exercise, exerciseEditBindingModel))
                .isEqualTo(expected);

    }

}
