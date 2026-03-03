package com.dimitarrradev.exercisesApi.exercise.service;

import com.dimitarrradev.exercisesApi.controller.binding.ExerciseAddModel;
import com.dimitarrradev.exercisesApi.controller.binding.ExerciseEditModel;
import com.dimitarrradev.exercisesApi.controller.binding.ImageUrlsAddModel;
import com.dimitarrradev.exercisesApi.error.exception.ExerciseAlreadyExistsException;
import com.dimitarrradev.exercisesApi.error.exception.ExerciseNotFoundException;
import com.dimitarrradev.exercisesApi.error.exception.ImageNotFoundException;
import com.dimitarrradev.exercisesApi.exercise.dao.ExerciseRepository;
import com.dimitarrradev.exercisesApi.exercise.dao.ImageUrlRepository;
import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.exercise.model.Exercise;
import com.dimitarrradev.exercisesApi.exercise.model.ExerciseModel;
import com.dimitarrradev.exercisesApi.exercise.model.ImageUrl;
import com.dimitarrradev.exercisesApi.exercise.model.ImageUrlModel;
import com.dimitarrradev.exercisesApi.exercise.util.ExerciseFromModelMapper;
import com.dimitarrradev.exercisesApi.exercise.util.ImageUrlFromModelMapper;
import com.dimitarrradev.exercisesApi.exercise.util.ImageUrlModelAssembler;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ExerciseServiceUnitTests {

    @MockitoBean
    private ExerciseRepository exerciseRepository;
    @MockitoBean
    private ImageUrlRepository imageUrlRepository;
    @MockitoBean
    private ImageUrlModelAssembler imageUrlModelAssembler;
    @MockitoBean
    private ExerciseFromModelMapper exerciseFromModelMapper;
    @MockitoBean
    private ImageUrlFromModelMapper imageUrlFromModelMapper;
    @Autowired
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
                new ArrayList<>(List.of(imageUrl)),
                null,
                null,
                false
        );
        imageUrl.setExercise(exercise);
    }


    @Test
    void testAddExerciseThrowsWhenNameFoundInRepository() {
        when(exerciseRepository.existsExerciseByNameAndIsDeletedFalse("test-exercise"))
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
    void testAddExerciseCreatesNewExercise() {
        when(exerciseRepository.existsExerciseByNameAndIsDeletedFalse("test-exercise-1"))
                .thenReturn(false);

        ExerciseAddModel addModel = new ExerciseAddModel(
                "test-exercise-1",
                "test-exercise-description",
                TargetBodyPart.ABDUCTORS,
                Complexity.EASY,
                MovementType.ISOLATION
        );

        Exercise exercise = Exercise.builder()
                .name(addModel.name())
                .description(addModel.description())
                .targetBodyPart(addModel.bodyPart())
                .complexity(addModel.complexity())
                .movementType(addModel.movement())
                .build();

        when(exerciseFromModelMapper.fromExerciseAddModel(addModel))
                .thenReturn(exercise);

        Exercise savedExercise = Exercise.builder()
                .id(2L)
                .name(addModel.name())
                .description(addModel.description())
                .targetBodyPart(addModel.bodyPart())
                .complexity(addModel.complexity())
                .movementType(addModel.movement())
                .isDeleted(Boolean.FALSE)
                .build();

        when(exerciseRepository.save(exercise))
                .thenReturn(savedExercise);

        ExerciseModel expected = toExerciseModel(savedExercise);

        ExerciseModel exerciseModel = exerciseService.addExercise(addModel);

        verify(exerciseRepository, times(1)).save(exercise);
        assertEquals(expected, exerciseModel);
    }

    @Test
    void testDeleteExerciseSetsIsDeletedToExerciseWhenFoundInRepositoryAndReturnsCorrectModel() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        ExerciseModel expected = toExerciseModel(exercise);
        expected.setIsDeleted(Boolean.TRUE);

        ExerciseModel exerciseModel = exerciseService.deleteExercise(1L);

        assertTrue(exercise.getIsDeleted());
        assertEquals(expected, exerciseModel);
        verify(exerciseRepository, Mockito.times(1))
                .saveAndFlush(exercise);
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

        ExerciseModel expected = toExerciseModel(exercise);

        assertEquals(expected, exerciseService.getExerciseModel(1L));

    }

    @Nonnull
    private static ExerciseModel toExerciseModel(Exercise exercise) {
        return new ExerciseModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getComplexity(),
                exercise.getDescription(),
                exercise.getMovementType(),
                exercise.getTargetBodyPart(),
                exercise.getIsDeleted()
        );
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

        ExerciseEditModel editModel = new ExerciseEditModel(
                "test-exercise-updated",
                "test-exercise-description-updated",
                TargetBodyPart.ABDUCTORS,
                Complexity.EASY,
                MovementType.ISOLATION
        );

        when(exerciseRepository.saveAndFlush(exercise))
                .thenReturn(exercise);

        exerciseService.editExercise(exercise.getId(), editModel);

        verify(exerciseRepository, Mockito.times(1))
                .saveAndFlush(exercise);
    }

    @Test
    void testEditExerciseThrowsWhenNotFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.empty());

        ExerciseEditModel editModel = new ExerciseEditModel(
                "test-exercise-updated",
                "test-exercise-description-updated",
                TargetBodyPart.ABDUCTORS,
                Complexity.EASY,
                MovementType.ISOLATION
        );

        assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.editExercise(1L, editModel)
        );

    }

    @Test
    void testGetExercisesByTargetsWithTargetBodyPartsNotAllOrEmptyReturnsCorrectListOfExerciseNameAndIdViewModel() {
        TargetBodyPart randomTargetBodyPart = exercise.getTargetBodyPart();

        List<Exercise> exercises = generateExerciseList(20)
                .stream()
                .filter(ex -> ex.getTargetBodyPart().equals(randomTargetBodyPart))
                .toList();

        when(exerciseRepository.findAllByTargetBodyPartIsInAndIsDeletedFalse(List.of(randomTargetBodyPart)))
                .thenReturn(exercises);

        CollectionModel<ExerciseModel> expectedExercises = CollectionModel.of(mapToExerciseModelList(exercises));

        CollectionModel<ExerciseModel> exercisesViewByTargets = exerciseService.getExercisesForTargetBodyParts(List.of(randomTargetBodyPart));

        assertEquals(expectedExercises, exercisesViewByTargets);
    }

    @Test
    void testGetExerciseReturnsCorrectExerciseWhenFoundInRepository() {
        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        assertEquals(exercise, exerciseService.getExerciseEntity(1L));
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
    void testGetAllExercisesReturnsCorrectListOfExercises() {
        List<Exercise> exerciseList = generateExerciseList(5)
                .stream()
                .toList();

        List<ExerciseModel> mappedExercises = mapToExerciseModelList(exerciseList);

        when(exerciseRepository.findAll())
                .thenReturn(exerciseList);

        CollectionModel<ExerciseModel> expected = CollectionModel.of(mappedExercises);

        assertEquals(expected, exerciseService.getAllExercises());

    }

    @Test
    void testSearchExercisesByFilterReturnsCorrectCollectionModelOfExerciseModelWithExerciseName() {
        List<Exercise> exerciseList = generateExerciseList(12).stream()
                .filter(exercise -> exercise.getName().contains("1"))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList, pageable, exerciseList.size());

        when(exerciseRepository.findAllByNameContainingIgnoreCaseAndIsDeletedFalse(pageable, "1"))
                .thenReturn(exercisePage);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        PagedModel<ExerciseModel> actual = exerciseService.searchExercises("1", TargetBodyPart.ALL, Complexity.ALL, MovementType.ALL, 0, pageable.getPageSize(), "asc");

        PagedModel<ExerciseModel> expected = PagedModel.of(exerciseModelList, pageMetadata);
        expected.add(Link.of("http://localhost?page=0&size=10&sort=name,asc", "self"));

        assertEquals(expected, actual);

    }

    @Test
    void testSearchExercisesByFilterReturnsCorrectCollectionModelOfExerciseModelWithTargetBodyPart() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.ABS))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList, pageable, exerciseList.size());

        when(exerciseRepository.findAllByTargetBodyPartAndIsDeletedFalse(
                pageable,
                TargetBodyPart.ABS
        )).thenReturn(exercisePage);


        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );


        PagedModel<ExerciseModel> actual = exerciseService.searchExercises("", TargetBodyPart.ABS, Complexity.ALL, MovementType.ALL, 0, pageable.getPageSize(), "desc");

        PagedModel<ExerciseModel> expected = PagedModel.of(exerciseModelList, pageMetadata);
        expected.add(Link.of("http://localhost?page=0&size=10&sort=name,desc", "self"));

        assertEquals(expected, actual);

    }

    @Test
    void testSearchExercisesByFilterReturnsCorrectCollectionModelOfExerciseModelWithTargetBodyPartAndComplexity() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.ABS)
                                && exercise.getComplexity().equals(Complexity.HARD))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList, pageable, exerciseList.size());

        when(exerciseRepository.findAllByTargetBodyPartAndComplexityAndIsDeletedFalse(
                pageable,
                TargetBodyPart.ABS,
                Complexity.HARD
        )).thenReturn(exercisePage);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        PagedModel<ExerciseModel> actual = exerciseService.searchExercises("", TargetBodyPart.ABS, Complexity.HARD, MovementType.ALL, 0, pageable.getPageSize(), "desc");

        PagedModel<ExerciseModel> expected = PagedModel.of(exerciseModelList, pageMetadata);
        expected.add(Link.of("http://localhost?page=0&size=10&sort=name,desc", "self"));

        assertEquals(expected, actual);

    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseSearchViewModelWithTargetBodyPartAndMovementTypeByFilter() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.ABS)
                                && exercise.getMovementType().equals(MovementType.ISOLATION))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList, pageable, exerciseList.size());

        when(exerciseRepository.findAllByTargetBodyPartAndMovementTypeAndIsDeletedFalse(
                pageable,
                TargetBodyPart.ABS,
                MovementType.ISOLATION
        )).thenReturn(exercisePage);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        PagedModel<ExerciseModel> expected = PagedModel.of(exerciseModelList, pageMetadata);
        expected.add(Link.of("http://localhost?page=0&size=10&sort=name,desc", "self"));

        PagedModel<ExerciseModel> actual = exerciseService.searchExercises(
                "", TargetBodyPart.ABS, Complexity.ALL, MovementType.ISOLATION, 0, pageable.getPageSize(), "desc");

        assertEquals(expected, actual);

    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseSearchViewModelWithTargetBodyPartComplexityAndMovementTypeByFilter() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.ABS)
                                && exercise.getComplexity().equals(Complexity.HARD)
                                && exercise.getMovementType().equals(MovementType.ISOLATION)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList, pageable, exerciseList.size());

        when(exerciseRepository.findAllByTargetBodyPartAndComplexityAndMovementTypeAndIsDeletedFalse(
                pageable,
                TargetBodyPart.ABS,
                Complexity.HARD,
                MovementType.ISOLATION
        )).thenReturn(exercisePage);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        PagedModel<ExerciseModel> expected = PagedModel.of(exerciseModelList, pageMetadata);
        expected.add(Link.of("http://localhost?page=0&size=10&sort=name,desc", "self"));

        PagedModel<ExerciseModel> actual = exerciseService.searchExercises(
                "", TargetBodyPart.ABS, Complexity.HARD, MovementType.ISOLATION, 0, pageable.getPageSize(), "desc");

        assertEquals(expected, actual);

    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseSearchViewModelWithComplexityAndMovementTypeByFilter() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getComplexity().equals(Complexity.HARD)
                                && exercise.getMovementType().equals(MovementType.ISOLATION)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList, pageable, exerciseList.size());

        when(exerciseRepository.findAllByComplexityAndMovementTypeAndIsDeletedFalse(
                pageable,
                Complexity.HARD,
                MovementType.ISOLATION
        )).thenReturn(exercisePage);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        PagedModel<ExerciseModel> expected = PagedModel.of(exerciseModelList, pageMetadata);
        expected.add(Link.of("http://localhost?page=0&size=10&sort=name,asc", "self"));

        PagedModel<ExerciseModel> actual = exerciseService.searchExercises("", TargetBodyPart.ALL, Complexity.HARD, MovementType.ISOLATION, 0, pageable.getPageSize(), "asc");

        assertEquals(expected, actual);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseSearchViewModelWithComplexityByFilter() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getComplexity().equals(Complexity.HARD)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList, pageable, exerciseList.size());

        when(exerciseRepository.findAllByComplexityAndIsDeletedFalse(
                pageable,
                Complexity.HARD
        )).thenReturn(exercisePage);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        PagedModel<ExerciseModel> expected = PagedModel.of(exerciseModelList, pageMetadata);
        expected.add(Link.of("http://localhost?page=0&size=10&sort=name,asc", "self"));

        PagedModel<ExerciseModel> actual = exerciseService.searchExercises("", TargetBodyPart.ALL, Complexity.HARD, MovementType.ALL, 0, pageable.getPageSize(), "asc");

        assertEquals(expected, actual);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseSearchViewModelWithMovementTypePageByFilter() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getMovementType().equals(MovementType.ISOLATION)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        PageImpl<Exercise> exercisePage = new PageImpl<>(exerciseList, pageable, exerciseList.size());

        when(exerciseRepository.findAllByMovementTypeAndIsDeletedFalse(
                pageable,
                MovementType.ISOLATION
        )).thenReturn(exercisePage);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        PagedModel<ExerciseModel> expected = PagedModel.of(exerciseModelList, pageMetadata);
        expected.add(Link.of("http://localhost?page=0&size=10&sort=name,asc", "self"));

        PagedModel<ExerciseModel> actual = exerciseService.searchExercises("", TargetBodyPart.ALL, Complexity.ALL, MovementType.ISOLATION, 0, pageable.getPageSize(), "asc");

        assertEquals(expected, actual);

    }

    @Test
    void testFindExercisesPageReturnsCorrectCollectionModelOfExerciseSearchViewModelWithNoFiltersPageByFilter() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        PageImpl<Exercise> exercisePage = new PageImpl<>(exerciseList, pageable, exerciseList.size());

        when(exerciseRepository.findAllByIsDeletedFalse(
                pageable
        )).thenReturn(exercisePage);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        PagedModel<ExerciseModel> expected = PagedModel.of(exerciseModelList, pageMetadata);
        expected.add(Link.of("http://localhost?page=0&size=10&sort=name,asc", "self"));

        PagedModel<ExerciseModel> actual = exerciseService.searchExercises("", TargetBodyPart.ALL, Complexity.ALL, MovementType.ALL, 0, pageable.getPageSize(), "asc");

        assertEquals(expected, actual);

    }

    @Test
    void testGetImagesThrowsWhenExerciseIsNotFound() {
        when(exerciseRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.getImages(0L)
        );
    }

    @Test
    void testGetImagesThrowsWhenExerciseExists() {
        when(exerciseRepository.existsById(exercise.getId()))
                .thenReturn(true);

        when(imageUrlRepository.findByExercise_Id(exercise.getId()))
                .thenReturn(exercise.getImageURLs());

        List<ImageUrlModel> urlModelStream = exercise
                .getImageURLs()
                .stream()
                .map(url ->
                        new ImageUrlModel(
                                url.getId(),
                                url.getUrl(),
                                exercise.getId(),
                                Boolean.FALSE
                        )
                ).toList();


        when(imageUrlModelAssembler.toCollectionModel(exercise.getImageURLs()))
                .thenReturn(CollectionModel.of(urlModelStream));

        assertEquals(CollectionModel.of(urlModelStream),
                exerciseService.getImages(exercise.getId()));
    }

    @Test
    void testAddImagesThrowsWhenExerciseIsNotFound() {
        when(exerciseRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.addImages(0L, new ImageUrlsAddModel(List.of("https://some-url.com")))
        );
    }

    @Test
    void testAddImageCreatesNewImagesAndSavesItWhenExerciseExists() {
        when(exerciseRepository.findById(exercise.getId()))
                .thenReturn(Optional.of(exercise));

        ImageUrlsAddModel addModel = new ImageUrlsAddModel(List.of("https://some-image.com"));

        ImageUrl imageUrl = new ImageUrl(null, addModel.urls().getFirst(), null, Boolean.FALSE);

        List<ImageUrl> imageUrls = List.of(imageUrl);

        when(imageUrlFromModelMapper.fromImageUrlsAddModel(addModel))
                .thenReturn(List.of(imageUrl));

        ImageUrl savedImage = new ImageUrl(2L, addModel.urls().getFirst(), exercise, Boolean.FALSE);

        List<ImageUrl> savedImageUrls = List.of(savedImage);

        when(imageUrlRepository.saveAllAndFlush(imageUrls))
                .thenReturn(savedImageUrls);

        CollectionModel<ImageUrlModel> expectedModel = CollectionModel.of(
                List.of(
                        new ImageUrlModel(
                                savedImage.getId(),
                                savedImage.getUrl(),
                                savedImage.getExercise().getId(),
                                Boolean.FALSE
                        )
                )
        );


        when(imageUrlModelAssembler.toCollectionModel(savedImageUrls))
                .thenReturn(expectedModel);

        CollectionModel<ImageUrlModel> result = exerciseService.addImages(exercise.getId(), addModel);

        assertEquals(expectedModel, result);
        assertEquals(exercise, imageUrl.getExercise(), "Exercise should not be null!");
    }

    @Test
    void testGetImageThrowsWhenImageIsNotFound() {
        when(imageUrlRepository.findByIdAndExercise_id(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ImageNotFoundException.class,
                () -> exerciseService.getImage(0L, 0L));
    }

    @Test
    void testGetImageReturnsCorrectModel() {
        ImageUrl imageUrl = exercise.getImageURLs().getFirst();

        when(imageUrlRepository.findByIdAndExercise_id(exercise.getId(), imageUrl.getId()))
                .thenReturn(Optional.of(imageUrl));

        ImageUrlModel expected = new ImageUrlModel(imageUrl.getId(), imageUrl.getUrl(), exercise.getId(), Boolean.FALSE);

        when(imageUrlModelAssembler.toModel(imageUrl))
                .thenReturn(expected);

        ImageUrlModel imageUrlModel = exerciseService.getImage(imageUrl.getId(), exercise.getId());

        assertEquals(expected, imageUrlModel);
    }

    @Test
    void testDeleteImageThrowsWhenImageIsNotFound() {
        when(imageUrlRepository.findByIdAndExercise_id(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ImageNotFoundException.class,
                () -> exerciseService.deleteImage(0L, 0L));
    }

    @Test
    void testDeleteImageDeletesWhenFound() {
        ImageUrl imageUrl = exercise.getImageURLs().getFirst();

        when(imageUrlRepository.findByIdAndExercise_id(imageUrl.getId(), exercise.getId()))
                .thenReturn(Optional.of(imageUrl));

        ImageUrlModel expected = new ImageUrlModel(imageUrl.getId(), imageUrl.getUrl(), exercise.getId(), Boolean.TRUE);
        when(imageUrlModelAssembler.toModel(imageUrl))
                .thenReturn(expected);

        ImageUrlModel deletedImage = exerciseService.deleteImage(exercise.getId(), imageUrl.getId());

        verify(imageUrlRepository, times(1)).delete(imageUrl);
        assertTrue(imageUrl.getIsDeleted());
        assertEquals(expected, deletedImage);
    }

    private static List<ExerciseModel> mapToExerciseModelList(List<Exercise> exerciseList) {
        return exerciseList.stream()
                .map(exercise -> toExerciseModel(exercise)
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
                    Collections.emptyList(),
                    null,
                    null,
                    Boolean.FALSE
            );

            exercises.add(e);
        }

        return exercises;
    }

}
