package com.dimitarrradev.workoutScheduler.exercise;

import com.dimitarrradev.workoutScheduler.exercise.dao.ExerciseRepository;
import com.dimitarrradev.workoutScheduler.exercise.dao.ImageUrlRepository;
import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.ImageUrlViewModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.util.mapping.WorkoutSchedulerMapper;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseEditBindingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceUnitTests {

    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private ImageUrlRepository imageUrlRepository;
    @Mock
    private WorkoutSchedulerMapper<Exercise> workoutSchedulerMapper;
    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise exercise;

    @BeforeEach
    public void setup() {
        exercise = new Exercise();
        exercise.setId(1L);
        exercise.setName("test");
        exercise.setDescription("description");
        exercise.setComplexity(Complexity.EASY);
        exercise.setApproved(false);
        exercise.setAddedBy("username");
        exercise.setMovementType(MovementType.ISOLATION);
        exercise.setTargetBodyPart(TargetBodyPart.ABDUCTORS);
    }

    @Test
    void testAddExerciseForReviewAddsExerciseWhenNameNotFoundInRepository() {
        ExerciseAddBindingModel exerciseAddBindingModel = new ExerciseAddBindingModel("test",
                "description",
                TargetBodyPart.ALL,
                "username",
                Complexity.EASY,
                MovementType.COMPOUND
        );

        Exercise saved = new Exercise();
        saved.setName(exerciseAddBindingModel.exerciseName());
        saved.setDescription(exerciseAddBindingModel.description());
        saved.setComplexity(exerciseAddBindingModel.complexity());
        saved.setMovementType(exerciseAddBindingModel.movementType());
        saved.setTargetBodyPart(exerciseAddBindingModel.bodyPart());
        saved.setApproved(false);
        saved.setAddedBy(exerciseAddBindingModel.addedBy());

        Mockito.when(workoutSchedulerMapper.mapFrom(exerciseAddBindingModel)).thenReturn(saved);
        Mockito.when(exerciseRepository.existsExerciseByName("test")).thenReturn(false);

        exerciseService.addExerciseForReview(exerciseAddBindingModel);

        Mockito.verify(exerciseRepository, Mockito.times(1)).save(saved);
    }

    @Test
    void testAddExerciseForReviewThrowsWhenNameFoundInRepository() {
        ExerciseAddBindingModel exerciseAddBindingModel = new ExerciseAddBindingModel(
                "test",
                "description",
                TargetBodyPart.ALL,
                "username", Complexity.EASY,
                MovementType.COMPOUND
        );
        Mockito.when(exerciseRepository.existsExerciseByName("test")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> exerciseService.addExerciseForReview(exerciseAddBindingModel));
    }

    @Test
    void testGetExercisesForReviewCountReturnsCorrectCount() {
        Mockito.when(exerciseRepository.countAllByApprovedFalse()).thenReturn(3L);

        assertThat(exerciseService.getExercisesForReviewCount()).isEqualTo(3L);
    }

    @Test
    void testApproveExerciseSavesApprovedExerciseWhenFoundInRepository() {
        Mockito.when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        exerciseService.approveExercise(1L);
        assertThat(exercise.getApproved()).isEqualTo(true);

        Mockito.verify(exerciseRepository, Mockito.times(1)).save(exercise);
    }

    @Test
    void testApproveExerciseThrowsWhenExerciseIsNotFound() {
        Mockito.when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.approveExercise(1L));
    }

    @Test
    void testDeleteExerciseDeletesExerciseWhenFoundInRepository() {
        Mockito.when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        exerciseService.deleteExercise(1L);

        Mockito.verify(exerciseRepository, Mockito.times(1)).delete(exercise);
    }

    @Test
    void testDeleteExerciseThrowsWhenNotFoundInRepository() {
        Mockito.when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.deleteExercise(1L));
    }

    @Test
    void testGetExerciseViewReturnsCorrectExerciseViewModelWhenFoundInRepository() {
        Mockito.when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        ExerciseViewModel viewModel = new ExerciseViewModel(
                exercise.getName(),
                exercise.getComplexity().getName(),
                exercise.getMovementType().getName(),
                exercise.getDescription(),
                exercise.getImageURLs().stream().map(imageUrl -> new ImageUrlViewModel(
                        imageUrl.getId(),
                        imageUrl.getUrl()
                )).toList()
        );

        assertThat(exerciseService.getExerciseView(1L)).isEqualTo(viewModel);
    }

    @Test
    void testGetExerciseViewThrowsWhenNotFoundInRepository() {
        Mockito.when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.getExerciseView(1L));
    }

    @Test
    void testEditExerciseSavesExerciseWhenFoundInRepository() {
        Mockito.when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        exerciseService.editExercise(new ExerciseEditBindingModel(1L, "new name", "description", "imageurl", true));
        assertThat(exercise.getName()).isEqualTo("new name");
        assertThat(exercise.getDescription()).isEqualTo("description");
        assertThat(exercise.getImageURLs()).hasSize(2);
        assertThat(exercise.getApproved()).isEqualTo(true);
        Mockito.verify(exerciseRepository, Mockito.times(1)).save(exercise);
    }

}
