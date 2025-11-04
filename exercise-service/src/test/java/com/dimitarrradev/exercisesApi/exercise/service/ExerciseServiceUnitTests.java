package com.dimitarrradev.exercisesApi.exercise.service;

import com.dimitarrradev.exercisesApi.exercise.Exercise;
import com.dimitarrradev.exercisesApi.exercise.dao.ExerciseRepository;
import com.dimitarrradev.exercisesApi.exercise.dto.*;
import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.image.ImageUrl;
import com.dimitarrradev.exercisesApi.image.dto.ImageUrlViewModel;
import com.dimitarrradev.exercisesApi.util.error.message.exception.ExerciseAlreadyExistsException;
import com.dimitarrradev.exercisesApi.util.error.message.exception.ExerciseNotFoundException;
import com.dimitarrradev.exercisesApi.util.mapping.ExerciseFromBindingModelMapper;
import com.dimitarrradev.exercisesApi.util.mapping.ExerciseToViewModelMapper;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseEditBindingModel;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseFindBindingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceUnitTests {

    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private ExerciseFromBindingModelMapper mapperFrom;
    @Mock
    private ExerciseToViewModelMapper mapperTo;
    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise exercise;

    @BeforeEach
    public void setup() {
        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setUrl("image-url");
        imageUrl.setId(11L);
        exercise = new Exercise(
                1L,
                "test-exercise-description",
                TargetBodyPart.ABDUCTORS,
                MovementType.COMPOUND,
                "test-exercise-description",
                new ArrayList<>(List.of(imageUrl)),
                Boolean.FALSE,
                "user",
                Complexity.EASY
        );

    }

    @Test
    void testAddExerciseForReviewAddsExerciseWhenNameNotFoundInRepository() {
        ExerciseAddBindingModel exerciseAddBindingModel = new ExerciseAddBindingModel("test-exercise",
                "test-exercise-description",
                TargetBodyPart.ABDUCTORS,
                "username",
                Complexity.EASY,
                MovementType.COMPOUND
        );

        Exercise saved = new Exercise(
                null,
                exerciseAddBindingModel.exerciseName(),
                exerciseAddBindingModel.bodyPart(),
                exerciseAddBindingModel.movementType(),
                exerciseAddBindingModel.description(),
                null,
                false,
                exerciseAddBindingModel.addedBy(),
                exerciseAddBindingModel.complexity()
        );

        when(mapperFrom.fromExerciseAddBindingModel(exerciseAddBindingModel))
                .thenReturn(saved);

        when(exerciseRepository.existsExerciseByName(exerciseAddBindingModel.exerciseName()))
                .thenReturn(false);

        when(exerciseRepository.save(saved))
                .thenReturn(saved);

        exerciseService.addExerciseForReview(exerciseAddBindingModel);

        verify(exerciseRepository, Mockito.times(1))
                .save(saved);
    }

    @Test
    void testAddExerciseForReviewThrowsWhenNameFoundInRepository() {
        ExerciseAddBindingModel exerciseAddBindingModel = new ExerciseAddBindingModel("test-exercise",
                "test-exercise-description",
                TargetBodyPart.ABDUCTORS,
                "username",
                Complexity.EASY,
                MovementType.COMPOUND
        );

        when(exerciseRepository.existsExerciseByName(exerciseAddBindingModel.exerciseName()))
                .thenReturn(true);

        assertThrows(
                ExerciseAlreadyExistsException.class,
                () -> exerciseService.addExerciseForReview(exerciseAddBindingModel)
        );
    }

    @Test
    void testGetExercisesForReviewCountReturnsCorrectCount() {
        when(exerciseRepository.countAllByApprovedFalse())
                .thenReturn(3L);

        assertThat(exerciseService.getExercisesForReviewCount()).isEqualTo(3L);
    }

    @Test
    void testApproveExerciseSavesApprovedExerciseWhenFoundInRepository() {
        when(exerciseRepository.findById(exercise.getId()))
                .thenReturn(Optional.of(exercise));

        Exercise toSave = new Exercise(
                exercise.getId(),
                exercise.getName(),
                exercise.getTargetBodyPart(),
                exercise.getMovementType(),
                exercise.getDescription(),
                exercise.getImageURLs(),
                Boolean.TRUE,
                exercise.getAddedBy(),
                exercise.getComplexity()
        );

        exerciseService.approveExercise(exercise.getId());

        verify(exerciseRepository, Mockito.times(1))
                .save(toSave);
    }

    @Test
    void testApproveExerciseThrowsWhenExerciseIsNotFound() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.approveExercise(1L)
        );
    }

    @Test
    void testDeleteExerciseDeletesExerciseWhenFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        exerciseService.deleteExercise(1L);

        verify(exerciseRepository, Mockito.times(1))
                .delete(exercise);
    }

    @Test
    void testDeleteExerciseThrowsWhenNotFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.deleteExercise(1L)
        );
    }

    @Test
    void testGetExerciseViewReturnsCorrectExerciseViewModelWhenFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        ExerciseViewModel expected = new ExerciseViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getComplexity().getName(),
                exercise.getMovementType().getName(),
                exercise.getDescription(),
                exercise.getAddedBy(),
                exercise.getApproved(),
                exercise.getImageURLs().stream().map(imageUrl -> new ImageUrlViewModel(
                        imageUrl.getId(),
                        imageUrl.getUrl()
                )).toList()
        );

        when(mapperTo.toExerciseViewModel(exercise))
                .thenReturn(expected);

        assertThat(exerciseService.getExerciseView(1L))
                .isEqualTo(expected);
    }

    @Test
    void testGetExerciseViewThrowsWhenNotFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.getExerciseView(1L)
        );
    }

    @Test
    void testEditExerciseSavesExerciseWhenFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        ExerciseEditBindingModel exerciseEdit = new ExerciseEditBindingModel(
                1L,
                "test-exercise",
                "test-exercise-description",
                "NewImageUrl",
                true);

        List<ImageUrl> exerciseEditImageUrls = Arrays.stream(exerciseEdit.addImageUrls().split(System.lineSeparator()))
                .map(url -> {
                    ImageUrl imageUrl = new ImageUrl();
                    imageUrl.setUrl(url.trim());
                    imageUrl.setExercise(exercise);
                    return imageUrl;
                }).toList();

        List<ImageUrl> imageUrls = new ArrayList<>(exercise.getImageURLs());
        imageUrls.addAll(exerciseEditImageUrls);

        Exercise toSave = new Exercise(
                exercise.getId(),
                exerciseEdit.name(),
                exercise.getTargetBodyPart(),
                exercise.getMovementType(),
                exerciseEdit.description(),
                imageUrls,
                exerciseEdit.approved(),
                exercise.getAddedBy(),
                exercise.getComplexity()
        );

        when(mapperFrom.fromExerciseEditBindingModel(exercise, exerciseEdit))
                .thenReturn(toSave);

        when(exerciseRepository.save(toSave))
                .thenReturn(toSave);

        ExerciseViewModel viewModel = exerciseService.editExercise(1L, exerciseEdit);

        verify(exerciseRepository, Mockito.times(1))
                .save(toSave);
    }

    @Test
    void testEditExerciseThrowsWhenNotFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.editExercise(1L, new ExerciseEditBindingModel(1L, null, null, null, null))
        );
    }

    @Test
    void testGetExerciseEditBindingModelReturnsCorrectExerciseViewModelWhenFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        ExerciseEditBindingModel expected = new ExerciseEditBindingModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                null,
                exercise.getApproved()
        );

        when(mapperTo.toExerciseEditBindingModel(exercise))
                .thenReturn(expected);

        assertThat(exerciseService.getExerciseEditBindingModel(1L))
                .isEqualTo(expected);
    }

    @Test
    public void testGetExerciseEditBindingModelThrowsWhenNotFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.getExerciseEditBindingModel(1L)
        );
    }


    @Test
    void testGetExercisesViewByTargetsWithTargetBodyPartsNotAllOrEmptyReturnsCorrectListOfExerciseNameAndIdViewModel() {
        TargetBodyPart randomTargetBodyPart = exercise.getTargetBodyPart();

        List<Exercise> exercises = generateExerciseList(20)
                .stream()
                .filter(ex -> ex.getTargetBodyPart().equals(randomTargetBodyPart))
                .toList();

        when(exerciseRepository.findAllByApprovedTrueAndTargetBodyPartIsIn(List.of(randomTargetBodyPart)))
                .thenReturn(exercises);

        List<ExerciseNameAndIdViewModel> expectedExercises = exercises
                .stream()
                .map(ex -> new ExerciseNameAndIdViewModel(ex.getId(), ex.getName()))
                .toList();

        exercises.forEach(ex -> {
            when(mapperTo.toExerciseNameAndIdViewModel(ex))
                    .thenReturn(new ExerciseNameAndIdViewModel(ex.getId(), ex.getName()));
        });

        List<ExerciseNameAndIdViewModel> exercisesViewByTargets = exerciseService.getExercisesForTargetBodyParts(List.of(randomTargetBodyPart));

        assertThat(exercisesViewByTargets)
                .isEqualTo(expectedExercises);
    }

    @Test
    void testGetExerciseReturnsCorrectExerciseWhenFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        assertThat(exerciseService.getExercise(1L))
                .isEqualTo(exercise);
    }

    @Test
    void testGetExerciseReturnsCorrectExerciseWhenNotFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.getExercise(1L)
        );
    }

    @Test
    void testGetExercisesForReviewPageReturnsCorrectListOfExercisesForReviewViewModel() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> page = new PageImpl<>(generateExerciseList(6));
        when(exerciseRepository.findAllByApprovedIsAndNameContainingIgnoreCase(pageable, false, ""))
                .thenReturn(page);

        Page<ExerciseForReviewViewModel> expected = page.map(exercise -> new ExerciseForReviewViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getComplexity(),
                exercise.getMovementType(),
                exercise.getAddedBy()
        ));

        page.getContent().forEach(exercise -> {
            when(mapperTo.toExerciseForReviewViewModel(exercise))
                    .thenReturn(new ExerciseForReviewViewModel(
                            exercise.getId(),
                            exercise.getName(),
                            exercise.getDescription(),
                            exercise.getComplexity(),
                            exercise.getMovementType(),
                            exercise.getAddedBy()
                    ));
        });

        assertThat(exerciseService.getExercisesForReviewPage(1, 10, "asc"))
                .isEqualTo(expected);
    }

    @Test
    void testApproveExerciseThrowsWhenExerciseNotFoundInRepository() {
        when(exerciseRepository.findById(exercise.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.approveExercise(exercise.getId())
        );
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

        assertThat(exerciseService.getPageInfo(page))
                .isEqualTo(expected);
    }

    @Test
    void testGetAllActiveExercisesReturnsCorrectListOfActiveExercises() {
        List<Exercise> exerciseList = generateExerciseList(5)
                .stream()
                .filter(exercise -> exercise.getApproved().equals(Boolean.TRUE))
                .toList();

        List<ExerciseFindViewModel> expected = mapToExerciseFindViewModelLst(exerciseList);

        when(exerciseRepository.findAllByApprovedTrue())
                .thenReturn(exerciseList);

        addMapperStubbings(exerciseList);

        assertThat(exerciseService.getAllActiveExercises())
                .isEqualTo(expected);
    }

    @Test
    void testFindActiveExercisesPageReturnsCorrectPageOfExerciseFindViewModelWithExerciseName() {
        ExerciseFindBindingModel exerciseFind = new ExerciseFindBindingModel("1", TargetBodyPart.ALL, Complexity.ALL, MovementType.ALL);

        List<Exercise> exerciseList = generateExerciseList(12).stream()
                .filter(exercise -> exercise.getName().contains(exerciseFind.name())
                        && exercise.getApproved().equals(Boolean.TRUE))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseFindViewModel> exerciseFindList = mapToExerciseFindViewModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<ExerciseFindViewModel> expected = new PageImpl<>(exerciseFindList);

        when(exerciseRepository.findAllByApprovedTrueAndNameContainingIgnoreCase(pageable, exerciseFind.name()))
                .thenReturn(new PageImpl<>(exerciseList));

        addMapperStubbings(exerciseList);

        assertThat(exerciseService.findActiveExercisesPage(exerciseFind, 1, pageable.getPageSize(), "asc").getContent())
                .isEqualTo(expected.getContent());
    }

    @Test
    void testFindActiveExercisesPageReturnsCorrectPageOfExerciseFindViewModelWithTargetBodyPart() {
        ExerciseFindBindingModel exerciseFind = new ExerciseFindBindingModel(null, TargetBodyPart.BACK, Complexity.ALL, null);

        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(exerciseFind.targetBodyPart())
                                && exercise.getApproved().equals(Boolean.TRUE))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseFindViewModel> exerciseFindList = mapToExerciseFindViewModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<ExerciseFindViewModel> expected = new PageImpl<>(exerciseFindList);

        when(exerciseRepository.findAllByApprovedTrueAndTargetBodyPart(
                pageable,
                exerciseFind.targetBodyPart()
        )).thenReturn(new PageImpl<>(exerciseList));

        addMapperStubbings(exerciseList);

        assertThat(exerciseService.findActiveExercisesPage(exerciseFind, 1, pageable.getPageSize(), "desc").getContent())
                .isEqualTo(expected.getContent());
    }

    @Test
    void testFindActiveExercisesPageReturnsCorrectPageOfExerciseFindViewModelWithTargetBodyPartAndComplexity() {
        ExerciseFindBindingModel exerciseFind = new ExerciseFindBindingModel(null, TargetBodyPart.LEGS, Complexity.EASY, MovementType.ALL);

        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(exerciseFind.targetBodyPart())
                                && exercise.getComplexity().equals(exerciseFind.complexity())
                                && exercise.getApproved().equals(Boolean.TRUE))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseFindViewModel> exerciseFindList = mapToExerciseFindViewModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<ExerciseFindViewModel> expected = new PageImpl<>(exerciseFindList);

        when(exerciseRepository.findAllByApprovedTrueAndTargetBodyPartAndComplexity(
                pageable,
                exerciseFind.targetBodyPart(),
                exerciseFind.complexity()
        )).thenReturn(new PageImpl<>(exerciseList));

        addMapperStubbings(exerciseList);

        assertThat(exerciseService.findActiveExercisesPage(exerciseFind, 1, pageable.getPageSize(), "desc").getContent())
                .isEqualTo(expected.getContent());
    }

    @Test
    void testFindActiveExercisesPageReturnsCorrectPageOfExerciseFindViewModelWithTargetBodyPartAndMovementType() {
        ExerciseFindBindingModel exerciseFind = new ExerciseFindBindingModel("", TargetBodyPart.LEGS, Complexity.ALL, MovementType.ISOLATION);

        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(exerciseFind.targetBodyPart())
                                && exercise.getMovementType().equals(exerciseFind.movementType())
                                && exercise.getApproved().equals(Boolean.TRUE))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseFindViewModel> exerciseFindList = mapToExerciseFindViewModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<ExerciseFindViewModel> expected = new PageImpl<>(exerciseFindList);

        when(exerciseRepository.findAllByApprovedTrueAndTargetBodyPartAndMovementType(
                pageable,
                exerciseFind.targetBodyPart(),
                exerciseFind.movementType()
        )).thenReturn(new PageImpl<>(exerciseList));

        addMapperStubbings(exerciseList);

        assertThat(exerciseService.findActiveExercisesPage(exerciseFind, 1, pageable.getPageSize(), "desc").getContent())
                .isEqualTo(expected.getContent());
    }

    @Test
    void testFindActiveExercisesPageReturnsCorrectPageOfExerciseFindViewModelWithTargetBodyPartComplexityAndMovementType() {
        ExerciseFindBindingModel exerciseFind = new ExerciseFindBindingModel(null, TargetBodyPart.LEGS, Complexity.EASY, MovementType.ISOLATION);

        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(exerciseFind.targetBodyPart())
                                && exercise.getComplexity().equals(exerciseFind.complexity())
                                && exercise.getMovementType().equals(exerciseFind.movementType())
                                && exercise.getApproved().equals(Boolean.TRUE)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseFindViewModel> exerciseFindList = mapToExerciseFindViewModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<ExerciseFindViewModel> expected = new PageImpl<>(exerciseFindList);

        when(exerciseRepository.findAllByApprovedTrueAndTargetBodyPartAndComplexityAndMovementType(
                pageable,
                exerciseFind.targetBodyPart(),
                exerciseFind.complexity(),
                exerciseFind.movementType()
        )).thenReturn(new PageImpl<>(exerciseList));

        addMapperStubbings(exerciseList);

        assertThat(exerciseService.findActiveExercisesPage(exerciseFind, 1, pageable.getPageSize(), "desc").getContent())
                .isEqualTo(expected.getContent());
    }

    @Test
    void testFindActiveExercisesPageReturnsCorrectPageOfExerciseFindViewModelWithComplexityAndMovementType() {
        ExerciseFindBindingModel exerciseFind = new ExerciseFindBindingModel(null, TargetBodyPart.ALL, Complexity.EASY, MovementType.ISOLATION);

        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getComplexity().equals(exerciseFind.complexity())
                                && exercise.getMovementType().equals(exerciseFind.movementType())
                                && exercise.getApproved().equals(Boolean.TRUE)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseFindViewModel> exerciseFindList = mapToExerciseFindViewModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<ExerciseFindViewModel> expected = new PageImpl<>(exerciseFindList);

        when(exerciseRepository.findAllByApprovedTrueAndComplexityAndMovementType(
                pageable,
                exerciseFind.complexity(),
                exerciseFind.movementType()
        )).thenReturn(new PageImpl<>(exerciseList));

        addMapperStubbings(exerciseList);

        assertThat(exerciseService.findActiveExercisesPage(exerciseFind, 1, pageable.getPageSize(), "asc").getContent())
                .isEqualTo(expected.getContent());
    }

    @Test
    void testFindActiveExercisesPageReturnsCorrectPageOfExerciseFindViewModelWithComplexity() {
        ExerciseFindBindingModel exerciseFind = new ExerciseFindBindingModel(null, null, Complexity.EASY, MovementType.ALL);

        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getComplexity().equals(exerciseFind.complexity())
                                && exercise.getApproved().equals(Boolean.TRUE)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseFindViewModel> exerciseFindList = mapToExerciseFindViewModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<ExerciseFindViewModel> expected = new PageImpl<>(exerciseFindList);

        when(exerciseRepository.findAllByApprovedTrueAndComplexity(
                pageable,
                exerciseFind.complexity()
        )).thenReturn(new PageImpl<>(exerciseList));

        addMapperStubbings(exerciseList);

        assertThat(exerciseService.findActiveExercisesPage(exerciseFind, 1, pageable.getPageSize(), "asc").getContent())
                .isEqualTo(expected.getContent());
    }

    @Test
    void testFindActiveExercisesPageReturnsCorrectPageOfExerciseFindViewModelWithMovementType() {
        ExerciseFindBindingModel exerciseFind = new ExerciseFindBindingModel(null, null, null, MovementType.ISOLATION);

        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getMovementType().equals(exerciseFind.movementType())
                                && exercise.getApproved().equals(Boolean.TRUE)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseFindViewModel> exerciseFindList = mapToExerciseFindViewModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<ExerciseFindViewModel> expected = new PageImpl<>(exerciseFindList);

        when(exerciseRepository.findAllByApprovedTrueAndMovementType(
                pageable,
                exerciseFind.movementType()
        )).thenReturn(new PageImpl<>(exerciseList));

        addMapperStubbings(exerciseList);

        assertThat(exerciseService.findActiveExercisesPage(exerciseFind, 1, pageable.getPageSize(), "asc").getContent())
                .isEqualTo(expected.getContent());
    }

    @Test
    void testFindActiveExercisesPageReturnsCorrectPageOfExerciseFindViewModelWithNoFilters() {
        ExerciseFindBindingModel exerciseFind = new ExerciseFindBindingModel(null, null, null, null);

        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getApproved().equals(Boolean.TRUE)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseFindViewModel> exerciseFindList = mapToExerciseFindViewModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<ExerciseFindViewModel> expected = new PageImpl<>(exerciseFindList);

        when(exerciseRepository.findAllByApprovedTrue(
                pageable
        )).thenReturn(new PageImpl<>(exerciseList));

        addMapperStubbings(exerciseList);

        assertThat(exerciseService.findActiveExercisesPage(exerciseFind, 1, pageable.getPageSize(), "asc").getContent())
                .isEqualTo(expected.getContent());
    }

    private void addMapperStubbings(List<Exercise> exerciseList) {
        exerciseList.forEach(exercise -> {
            when(mapperTo.toExerciseFindViewModel(exercise))
                    .thenReturn(new ExerciseFindViewModel(
                            exercise.getId(),
                            exercise.getName(),
                            exercise.getComplexity(),
                            exercise.getMovementType())
                    );
        });
    }

    private static List<ExerciseFindViewModel> mapToExerciseFindViewModelLst(List<Exercise> exerciseList) {
        return exerciseList.stream()
                .map(exercise -> new ExerciseFindViewModel(
                        exercise.getId(),
                        exercise.getName(),
                        exercise.getComplexity(),
                        exercise.getMovementType()
                )).toList();
    }

    private List<Exercise> generateExerciseList(int count) {
        List<Exercise> exercises = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Exercise e = new Exercise(
                    i + 1L,
                    "test-exercise-" + i,
                    i % 2 == 0 ? exercise.getTargetBodyPart() : TargetBodyPart.ABS,
                    i % 2 == 0 ? exercise.getMovementType() : MovementType.ISOLATION,
                    "test-exercise-description-" + i,
                    Collections.emptyList(),
                    i % 2 == 0 ? exercise.getApproved() : Boolean.FALSE,
                    "user" + i,
                    i % 2 == 0 ? exercise.getComplexity() : Complexity.HARD
            );

            exercises.add(e);
        }

        return exercises;
    }

}
