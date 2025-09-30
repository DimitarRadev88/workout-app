package com.dimitarrradev.workoutScheduler.util.mapping.exercise;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.ImageUrl;
import com.dimitarrradev.workoutScheduler.exercise.dto.*;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseEditBindingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
                "Test Exercise",
                getRandomTargetBodyPart(),
                getRandomMovementType(),
                "Test Exercise Description",
                List.of(new ImageUrl(22L, "http://exercise.img", null)),
                Boolean.FALSE,
                "Test user",
                getRandomComplexity()
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
                exercise.getName(),
                exercise.getComplexity().getName(),
                exercise.getMovementType().getName(),
                exercise.getDescription(),
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

        ExerciseEditBindingModel actual = mapper.toExerciseEditBindingModel(exercise);

        assertThat(actual).isEqualTo(expected);
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

    private Complexity getRandomComplexity() {
        return Complexity.values()[ThreadLocalRandom.current().nextInt(Complexity.values().length - 1)];
    }

    private MovementType getRandomMovementType() {
        return MovementType.values()[ThreadLocalRandom.current().nextInt(MovementType.values().length - 1)];
    }

    private TargetBodyPart getRandomTargetBodyPart() {
        return TargetBodyPart.values()[ThreadLocalRandom.current().nextInt(TargetBodyPart.values().length - 1)];
    }
}
