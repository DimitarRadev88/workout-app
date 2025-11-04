package com.dimitarrradev.exercisesApi.mapping.exercise;

import com.dimitarrradev.exercisesApi.exercise.Exercise;
import com.dimitarrradev.exercisesApi.image.ImageUrl;
import com.dimitarrradev.exercisesApi.image.dto.ImageUrlViewModel;
import com.dimitarrradev.exercisesApi.util.mapping.ExerciseToViewModelMapper;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseEditBindingModel;
import com.dimitarrradev.exercisesApi.exercise.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.dimitarrradev.exercisesApi.RandomValueGenerator.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class ExerciseToViewModelMapperUnitTests {

    @InjectMocks
    private ExerciseToViewModelMapper mapper;

    private Exercise exercise;

    @BeforeEach
    void setup() {
        exercise = new Exercise(
                11L,
                randomExerciseName(),
                randomTargetBodyPart(),
                randomMovementType(),
                randomDescription(),
                List.of(new ImageUrl(22L, "http://exercise.img", null)),
                Boolean.FALSE,
                "Test user",
                randomComplexity()
        );
    }

    @Test
    void testToExerciseFindViewModelReturnsModelWithCorrectData() {
        ExerciseFindViewModel expected = new ExerciseFindViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getComplexity(),
                exercise.getMovementType()
        );

        ExerciseFindViewModel actual = mapper.toExerciseFindViewModel(exercise);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testToExerciseViewModelReturnsModelWithCorrectData() {
        ExerciseViewModel expected = new ExerciseViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getComplexity().getName(),
                exercise.getMovementType().getName(),
                exercise.getDescription(),
                exercise.getAddedBy(),
                exercise.getApproved(),
                exercise.getImageURLs().stream()
                        .map(url -> new ImageUrlViewModel(url.getId(), url.getUrl()))
                        .toList()
        );

        ExerciseViewModel actual = mapper.toExerciseViewModel(exercise);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testToExerciseEditBindingModelReturnsModelWithCorrectData() {
        ExerciseEditBindingModel expected = new ExerciseEditBindingModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                null,
                exercise.getApproved()
        );

        ExerciseEditBindingModel exerciseEditBindingModel = mapper.toExerciseEditBindingModel(exercise);

        assertThat(exerciseEditBindingModel).isEqualTo(expected);
    }

    @Test
    void testToExerciseNameAndIdViewModelReturnsModelWithCorrectData() {
        ExerciseNameAndIdViewModel expected = new ExerciseNameAndIdViewModel(
                exercise.getId(),
                exercise.getName()
        );

        ExerciseNameAndIdViewModel actual = mapper.toExerciseNameAndIdViewModel(exercise);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testToExerciseForReviewViewModelReturnsModelWithCorrectData() {
        ExerciseForReviewViewModel expected = new ExerciseForReviewViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getComplexity(),
                exercise.getMovementType(),
                exercise.getAddedBy()
        );

        ExerciseForReviewViewModel actual = mapper.toExerciseForReviewViewModel(exercise);

        assertThat(actual).isEqualTo(expected);
    }

}
