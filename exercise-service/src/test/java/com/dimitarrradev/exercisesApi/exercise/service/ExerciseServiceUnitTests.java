package com.dimitarrradev.exercisesApi.exercise.service;

import com.dimitarrradev.exercisesApi.controller.binding.ExerciseAddModel;
import com.dimitarrradev.exercisesApi.error.exception.ExerciseAlreadyExistsException;
import com.dimitarrradev.exercisesApi.error.exception.ExerciseNotFoundException;
import com.dimitarrradev.exercisesApi.exercise.dao.ExerciseRepository;
import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.exercise.model.Exercise;
import com.dimitarrradev.exercisesApi.exercise.model.ExerciseModel;
import com.dimitarrradev.exercisesApi.exercise.model.ImageUrl;
import com.dimitarrradev.exercisesApi.exercise.util.ExerciseModelAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;

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
    private ExerciseModelAssembler assembler;
    @Mock
    private PagedResourcesAssembler<Exercise> pagedResourcesAssembler;
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
                "test-exercise-description",
                Complexity.EASY,
                TargetBodyPart.ABDUCTORS,
                MovementType.COMPOUND,
                new ArrayList<>(List.of(imageUrl))
        );
    }


    @Test
    void testAddExerciseThrowsWhenNameFoundInRepository() {
        when(exerciseRepository.existsExerciseByName("test-exercise"))
                .thenReturn(true);

        ExerciseAddModel addModel = new ExerciseAddModel(
                "test-exercise",
                "test-exercise-description",
                TargetBodyPart.ABDUCTORS,
                Complexity.EASY,
                MovementType.ISOLATION
        );

        assertThrows(
                ExerciseAlreadyExistsException.class,
                () -> exerciseService.addExercise(addModel)
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
    void testGetExerciseReturnsCorrectExerciseViewModelWhenFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        ExerciseModel expected = new ExerciseModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getComplexity(),
                exercise.getDescription(),
                exercise.getMovementType(),
                exercise.getTargetBodyPart()
        );

        when(assembler.toModel(exercise))
                .thenReturn(expected);

        assertThat(exerciseService.getExerciseModel(1L))
                .isEqualTo(expected);
    }

    @Test
    void testGetExerciseViewThrowsWhenNotFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.getExerciseModel(1L)
        );
    }

    @Test
    void testEditExerciseSavesExerciseWhenFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        Exercise toSave = new Exercise(
                exercise.getId(),
                "test-exercise",
                "test-exercise-description",
                exercise.getComplexity(),
                exercise.getTargetBodyPart(),
                exercise.getMovementType(),
                exercise.getImageURLs()
        );

        exerciseService.editExercise(exercise.getId(), "test-exercise", "test-exercise-description");

        verify(exerciseRepository, Mockito.times(1))
                .save(toSave);
    }

    @Test
    void testEditExerciseThrowsWhenNotFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.editExercise(1L, "test-exercise", "test-exercise-description")
        );

    }

    @Test
    void testGetExercisesByTargetsWithTargetBodyPartsNotAllOrEmptyReturnsCorrectListOfExerciseNameAndIdViewModel() {
        TargetBodyPart randomTargetBodyPart = exercise.getTargetBodyPart();

        List<Exercise> exercises = generateExerciseList(20)
                .stream()
                .filter(ex -> ex.getTargetBodyPart().equals(randomTargetBodyPart))
                .toList();

        when(exerciseRepository.findAllByTargetBodyPartIsIn(List.of(randomTargetBodyPart)))
                .thenReturn(exercises);

        List<ExerciseModel> expectedExercises = exercises
                .stream()
                .map(ex -> new ExerciseModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity(),
                                exercise.getDescription(),
                                exercise.getMovementType(),
                                exercise.getTargetBodyPart()
                        )
                )
                .toList();

        exercises.forEach(ex -> {
            when(assembler.toModel(ex))
                    .thenReturn(new ExerciseModel(
                                    exercise.getId(),
                                    exercise.getName(),
                                    exercise.getComplexity(),
                                    exercise.getDescription(),
                                    exercise.getMovementType(),
                                    exercise.getTargetBodyPart()
                            )
                    );
        });

        List<ExerciseModel> exercisesViewByTargets = exerciseService.getExercisesForTargetBodyParts(List.of(randomTargetBodyPart));

        assertThat(exercisesViewByTargets)
                .isEqualTo(expectedExercises);
    }

    @Test
    void testGetExerciseReturnsCorrectExerciseWhenFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        assertThat(exerciseService.getExerciseEntity(1L))
                .isEqualTo(exercise);
    }

    @Test
    void testGetExerciseReturnsCorrectExerciseWhenNotFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.getExerciseEntity(1L)
        );
    }

    @Test
    void testGetExercisesReturnsCorrectListOfExercisesForReviewViewModel() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> page = new PageImpl<>(generateExerciseList(6));
        when(exerciseRepository.findAllBy(pageable))
                .thenReturn(page);

        Page<ExerciseModel> mappedExercisesPage = page.map(exercise -> new ExerciseModel(
                        exercise.getId(),
                        exercise.getName(),
                        exercise.getComplexity(),
                        exercise.getDescription(),
                        exercise.getMovementType(),
                        exercise.getTargetBodyPart()
                )
        );

        CollectionModel<ExerciseModel> model = CollectionModel.of(mappedExercisesPage);

        when(assembler.toCollectionModel(page)).thenReturn(model);

        throw new IllegalArgumentException("TODO");

    }

    @Test
    void testGetAllExercisesReturnsCorrectListOfExercises() {
        List<Exercise> exerciseList = generateExerciseList(5)
                .stream()
                .toList();

        List<ExerciseModel> mappedExercises = mapToExerciseModelLst(exerciseList);

        when(exerciseRepository.findAll())
                .thenReturn(exerciseList);

        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(mappedExercises);

        when(assembler.toCollectionModel(exerciseList)).thenReturn(collectionModel);

        assertThat(exerciseService.getAllExercises())
                .isEqualTo(collectionModel);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseModelWithExerciseName() {
        List<Exercise> exerciseList = generateExerciseList(12).stream()
                .filter(exercise -> exercise.getName().contains("1"))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> exerciseModelPage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAllByNameContainingIgnoreCase(pageable, "1"))
                .thenReturn(new PageImpl<>(exerciseList));

        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(exerciseModelList);

        when(assembler.toCollectionModel(exerciseModelPage)).thenReturn(collectionModel);

        assertThat(exerciseService.findExercises("1", "all", "all", "all", 1, pageable.getPageSize(), "asc"))
                .isEqualTo(collectionModel);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseModelWithTargetBodyPart() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.BACK))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAllByTargetBodyPart(
                pageable,
                TargetBodyPart.BACK
        )).thenReturn(exercisePage);


        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(exerciseModelList);

        when(assembler.toCollectionModel(exercisePage)).thenReturn(collectionModel);

        assertThat(exerciseService.findExercises("", "back", "all", "", 1, pageable.getPageSize(), "desc"))
                .isEqualTo(collectionModel);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseModelWithTargetBodyPartAndComplexity() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.LEGS)
                                && exercise.getComplexity().equals(Complexity.EASY))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        Page<ExerciseModel> exerciseModelPage = new PageImpl<>(exerciseModelList);

        when(exerciseRepository.findAllByTargetBodyPartAndComplexity(
                pageable,
                TargetBodyPart.LEGS,
                Complexity.EASY
        )).thenReturn(new PageImpl<>(exerciseList));


        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(exerciseModelPage);

        when(assembler.toCollectionModel(exercisePage)).thenReturn(collectionModel);


        assertThat(exerciseService.findExercises("", "legs", "easy", "all", 1, pageable.getPageSize(), "desc"))
                .isEqualTo(collectionModel);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseFindViewModelWithTargetBodyPartAndMovementType() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.LEGS)
                                && exercise.getMovementType().equals(MovementType.ISOLATION))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        Page<ExerciseModel> exerciseModelPage = new PageImpl<>(exerciseModelList);

        when(exerciseRepository.findAllByTargetBodyPartAndMovementType(
                pageable,
                TargetBodyPart.LEGS,
                MovementType.ISOLATION
        )).thenReturn(new PageImpl<>(exerciseList));

        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(exerciseModelPage);

        when(assembler.toCollectionModel(exercisePage)).thenReturn(collectionModel);

        assertThat(exerciseService.findExercises("", "legs", "", "isolation", 1, pageable.getPageSize(), "desc"))
                .isEqualTo(collectionModel);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseFindViewModelWithTargetBodyPartComplexityAndMovementType() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.LEGS)
                                && exercise.getComplexity().equals(Complexity.EASY)
                                && exercise.getMovementType().equals(MovementType.ISOLATION)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        Page<ExerciseModel> exerciseModelPage = new PageImpl<>(exerciseModelList);

        when(exerciseRepository.findAllByTargetBodyPartAndComplexityAndMovementType(
                pageable,
                TargetBodyPart.LEGS,
                Complexity.EASY,
                MovementType.ISOLATION
        )).thenReturn(new PageImpl<>(exerciseList));


        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(exerciseModelPage);

        when(assembler.toCollectionModel(exercisePage)).thenReturn(collectionModel);

        assertThat(exerciseService.findExercises("", "legs", "easy", "isolation", 1, pageable.getPageSize(), "desc"))
                .isEqualTo(collectionModel);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseFindViewModelWithComplexityAndMovementType() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getComplexity().equals(Complexity.EASY)
                                && exercise.getMovementType().equals(MovementType.ISOLATION)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        Page<ExerciseModel> exerciseModelPage = new PageImpl<>(exerciseModelList);

        when(exerciseRepository.findAllByComplexityAndMovementType(
                pageable,
                Complexity.EASY,
                MovementType.ISOLATION
        )).thenReturn(new PageImpl<>(exerciseList));

        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(exerciseModelPage);

        when(assembler.toCollectionModel(exercisePage)).thenReturn(collectionModel);

        assertThat(exerciseService.findExercises("", "all", "easy", "isolation", 1, pageable.getPageSize(), "asc"))
                .isEqualTo(collectionModel);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseFindViewModelWithComplexity() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getComplexity().equals(Complexity.EASY)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        Page<ExerciseModel> exerciseModelPage = new PageImpl<>(exerciseModelList);

        when(exerciseRepository.findAllByComplexity(
                pageable,
                Complexity.EASY
        )).thenReturn(new PageImpl<>(exerciseList));

        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(exerciseModelPage);

        when(assembler.toCollectionModel(exercisePage)).thenReturn(collectionModel);

        assertThat(exerciseService.findExercises("", "", "easy", "", 1, pageable.getPageSize(), "asc"))
                .isEqualTo(collectionModel);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseFindViewModelWithMovementTypePage() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getMovementType().equals(MovementType.ISOLATION)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseFindList = mapToExerciseModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        PageImpl<Exercise> exercisePage = new PageImpl<>(exerciseList);

        Page<ExerciseModel> exerciseModelPage = new PageImpl<>(exerciseFindList);

        when(exerciseRepository.findAllByMovementType(
                pageable,
                MovementType.ISOLATION
        )).thenReturn(new PageImpl<>(exerciseList));


        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(exerciseModelPage);

        when(assembler.toCollectionModel(exercisePage)).thenReturn(collectionModel);

        assertThat(exerciseService.findExercises("", "", "", "isolation", 1, pageable.getPageSize(), "asc"))
                .isEqualTo(collectionModel);
    }

    @Test
    void testFindExercisesPageReturnsCorrectCollectionModelOfExerciseFindViewModelWithNoFiltersPage() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseFindList = mapToExerciseModelLst(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<ExerciseModel> expected = new PageImpl<>(exerciseFindList);

        PageImpl<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAll(
                pageable
        )).thenReturn(exercisePage);

        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(expected);

        when(assembler.toCollectionModel(exercisePage)).thenReturn(collectionModel);

        assertThat(exerciseService.findExercises("", "", "", "", 1, pageable.getPageSize(), "asc"))
                .isEqualTo(collectionModel);
    }

    private static List<ExerciseModel> mapToExerciseModelLst(List<Exercise> exerciseList) {
        return exerciseList.stream()
                .map(exercise -> new ExerciseModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity(),
                                exercise.getDescription(),
                                exercise.getMovementType(),
                                exercise.getTargetBodyPart()
                        )
                ).toList();
    }

    private List<Exercise> generateExerciseList(int count) {
        List<Exercise> exercises = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Exercise e = new Exercise(
                    i + 1L,
                    "test-exercise-" + i,
                    "test-exercise-description-" + i,
                    i % 2 == 0 ? exercise.getComplexity() : Complexity.HARD,
                    i % 2 == 0 ? exercise.getTargetBodyPart() : TargetBodyPart.ABS,
                    i % 2 == 0 ? exercise.getMovementType() : MovementType.ISOLATION,
                    Collections.emptyList()
            );

            exercises.add(e);
        }

        return exercises;
    }

}
