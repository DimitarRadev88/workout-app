package com.dimitarrradev.workoutScheduler.exercise;

import com.dimitarrradev.workoutScheduler.exercise.dao.ExerciseRepository;
import com.dimitarrradev.workoutScheduler.exercise.dao.ImageUrlRepository;
import com.dimitarrradev.workoutScheduler.exercise.dto.*;
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
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private ImageUrl imageUrl;

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
        imageUrl = new ImageUrl();
        imageUrl.setUrl("image-url");
        imageUrl.setId(11L);
        exercise.setImageURLs(new ArrayList<>(List.of(imageUrl)));
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

        Mockito
                .when(workoutSchedulerMapper.mapFrom(exerciseAddBindingModel))
                .thenReturn(saved);
        Mockito
                .when(exerciseRepository.existsExerciseByName("test"))
                .thenReturn(false);

        exerciseService.addExerciseForReview(exerciseAddBindingModel);

        Mockito
                .verify(exerciseRepository, Mockito.times(1))
                .save(saved);
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
        Mockito
                .when(exerciseRepository.existsExerciseByName("test"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> exerciseService.addExerciseForReview(exerciseAddBindingModel));
    }

    @Test
    void testGetExercisesForReviewCountReturnsCorrectCount() {
        Mockito
                .when(exerciseRepository.countAllByApprovedFalse())
                .thenReturn(3L);

        assertThat(exerciseService.getExercisesForReviewCount()).isEqualTo(3L);
    }

    @Test
    void testApproveExerciseSavesApprovedExerciseWhenFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        exerciseService.approveExercise(1L);
        assertThat(exercise.getApproved()).isEqualTo(true);

        Mockito
                .verify(exerciseRepository, Mockito.times(1))
                .save(exercise);
    }

    @Test
    void testApproveExerciseThrowsWhenExerciseIsNotFound() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.approveExercise(1L));
    }

    @Test
    void testDeleteExerciseDeletesExerciseWhenFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        exerciseService.deleteExercise(1L);

        Mockito
                .verify(exerciseRepository, Mockito.times(1))
                .delete(exercise);
    }

    @Test
    void testDeleteExerciseThrowsWhenNotFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.deleteExercise(1L));
    }

    @Test
    void testGetExerciseViewReturnsCorrectExerciseViewModelWhenFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

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
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.getExerciseView(1L));
    }

    @Test
    void testEditExerciseSavesExerciseWhenFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        exerciseService.editExercise(new ExerciseEditBindingModel(1L, "new name", "new description", "NewImageUrl", true));
        assertThat(exercise.getName()).isEqualTo("new name");
        assertThat(exercise.getDescription()).isEqualTo("new description");
        assertThat(exercise.getImageURLs()).hasSize(2);
        assertThat(exercise.getApproved()).isEqualTo(true);
        Mockito
                .verify(exerciseRepository, Mockito.times(1))
                .save(exercise);
    }

    @Test
    void testEditExerciseThrowsWhenNotFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.editExercise(new ExerciseEditBindingModel(1L, null, null, null, null)));
    }

    @Test
    void testDeleteImageUrlDeletesUrlWhenFoundInRepository() {
        exerciseService.deleteImageUrl(11L);

        Mockito
                .verify(imageUrlRepository, Mockito.times(1))
                .deleteById(11L);
    }

    @Test
    void testGetExerciseEditBindingModelReturnsCorrectExerciseViewModelWhenFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        ExerciseEditBindingModel bindingModel = new ExerciseEditBindingModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                null,
                exercise.getApproved()
        );

        assertThat(exerciseService.getExerciseEditBindingModel(1L)).isEqualTo(bindingModel);
    }

    @Test
    public void testGetExerciseEditBindingModelThrowsWhenNotFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.getExerciseEditBindingModel(1L));
    }

    @Test
    void testGetExerciseImagesReturnsCorrectListOfImageUrlViewModel() {
        Mockito
                .when(imageUrlRepository.findByExercise_Id(1L))
                .thenReturn(exercise.getImageURLs());

        List<ImageUrlViewModel> imageUrlViewModels = exercise
                .getImageURLs().
                stream()
                .map(image -> new ImageUrlViewModel(image.getId(), image.getUrl()))
                .toList();

        List<ImageUrlViewModel> exerciseImages = exerciseService.getExerciseImages(1L);

        assertThat(imageUrlViewModels).isEqualTo(exerciseImages);
    }

    @Test
    void testGetExerciseImagesReturnsEmptyListWhenNoUrlsForExercise() {
        Mockito
                .when(imageUrlRepository.findByExercise_Id(1L))
                .thenReturn(Collections.emptyList());

        assertThat(exerciseService.getExerciseImages(1L)).isEmpty();
    }

    @Test
    void testGetExercisesViewByTargetsWithTargetBodyPartsNotAllOrEmptyReturnsCorrectListOfExerciseNameAndIdViewModel() {
        Exercise crunches = new Exercise();
        crunches.setId(22L);
        crunches.setName("Crunches");
        crunches.setApproved(true);
        crunches.setComplexity(Complexity.EASY);
        crunches.setTargetBodyPart(TargetBodyPart.ABS);
        crunches.setMovementType(MovementType.ISOLATION);


        List<Exercise> exercises = getExerciseList(10);


        Mockito
                .when(exerciseRepository.findAllByApprovedTrueAndTargetBodyPartIsIn(List.of(TargetBodyPart.ABS)))
                .thenReturn(exercises);

        List<ExerciseNameAndIdViewModel> expectedExercises = exercises
                .stream()
                .map(ex -> new ExerciseNameAndIdViewModel(ex.getId(), ex.getName()))
                .toList();

        List<ExerciseNameAndIdViewModel> exercisesViewByTargets = exerciseService.getExercisesForTargetBodyParts(List.of(TargetBodyPart.ABS));

        assertThat(exercisesViewByTargets).isEqualTo(expectedExercises);
    }

    @Test
    void testGetExerciseReturnsCorrectExerciseWhenFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        assertThat(exerciseService.getExercise(1L)).isEqualTo(exercise);
    }

    @Test
    void testGetExerciseReturnsCorrectExerciseWhenNotFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.getExercise(1L));
    }

    @Test
    void testGetExercisesForReviewPageReturnsCorrectListOfExercisesForReviewViewModel() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> page = new PageImpl<>(getExerciseList(6));
        Mockito
                .when(exerciseRepository.findAllByApprovedIsAndNameContainingIgnoreCase(pageable, false, ""))
                .thenReturn(page);

        Page<ExerciseForReviewViewModel> expected = page.map(exercise -> new ExerciseForReviewViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getComplexity(),
                exercise.getMovementType(),
                exercise.getAddedBy()
        ));

        assertThat(exerciseService.getExercisesForReviewPage(1, 10, "asc")).isEqualTo(expected);
    }

    @Test
    void testApproveExerciseSetsExerciseToApprovedAndSavesItWhenFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(exercise.getId()))
                .thenReturn(Optional.of(exercise));

        exerciseService.approveExercise(exercise.getId());

        assertThat(exercise.getApproved()).isEqualTo(true);
        Mockito
                .verify(exerciseRepository, Mockito.times(1))
                .save(exercise);
    }

    @Test
    void testApproveExerciseThrowsWhenExerciseNotFoundInRepository() {
        Mockito
                .when(exerciseRepository.findById(exercise.getId()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.approveExercise(exercise.getId()));
    }

    @Test
    void testGetPageInfoReturnsCorrectPageInformation() {
        Page<ExerciseForReviewViewModel> page = new PageImpl<>(List.of(new ExerciseForReviewViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getComplexity(),
                exercise.getMovementType(),
                exercise.getAddedBy()
        )));

        PageInformation expected = new PageInformation(
                String.format("Showing %d to %d of %d exercises",
                page.getTotalElements() == 0 ? 0 : Math.min(page.getTotalElements(), (long) (page.getNumber() + 1) * page.getSize()),
                (page.getNumber() + 1) < page.getTotalPages() ? (long) (page.getNumber() + 1) * page.getSize() : page.getTotalElements(),
                page.getTotalElements()
                ),
                List.of(5, 10, 25, 50)
        );

        assertThat(exerciseService.getPageInfo(page)).isEqualTo(expected);
    }

    @Test
    void testGetAllActiveExercisesReturnsCorrectListOfActiveExercises() {
        List<Exercise> exerciseList = getExerciseList(5)
                .stream()
                .filter(exercise -> exercise.getApproved().equals(Boolean.TRUE))
                .toList();

        List<ExerciseFindViewModel> expected = exerciseList.stream().map(exercise -> new ExerciseFindViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getComplexity(),
                exercise.getMovementType()
        )).toList();

        Mockito.when(exerciseRepository.findAllByApprovedTrue())
                .thenReturn(exerciseList);

        assertThat(exerciseService.getAllActiveExercises()).isEqualTo(expected);
    }

    private List<Exercise> getExerciseList(int count) {
        List<Exercise> exercises = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Exercise e = new Exercise();
            e.setId((long) (i + 1));
            e.setAddedBy("user" + i);
            e.setName("Exercise " + i);
            e.setDescription("Exercise description" + i);
            if (i % 2 == 0) {
                e.setApproved(true);
                e.setComplexity(Complexity.EASY);
                e.setMovementType(MovementType.ISOLATION);
            } else {
                e.setApproved(false);
                e.setComplexity(Complexity.MEDIUM);
                e.setMovementType(MovementType.ALL);
            }

            exercises.add(e);
        }

        return exercises;
    }

}
