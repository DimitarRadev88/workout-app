package com.dimitarrradev.exercisesApi.mapping.exercise;

import com.dimitarrradev.exercisesApi.exercise.Exercise;
import com.dimitarrradev.exercisesApi.exercise.dto.ExerciseModel;
import com.dimitarrradev.exercisesApi.image.ImageUrl;
import com.dimitarrradev.exercisesApi.image.dto.ImageUrlModel;
import com.dimitarrradev.exercisesApi.util.mapping.ExerciseToViewModelMapper;
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
    void testToExerciseModelReturnsModelWithCorrectData() {
        ExerciseModel expected = new ExerciseModel(
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

        ExerciseModel actual = mapper.toExerciseModel(exercise);

        assertThat(actual).isEqualTo(expected);
    }


}
