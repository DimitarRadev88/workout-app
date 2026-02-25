package com.dimitarrradev.exercisesApi.exercise.service;

import com.dimitarrradev.exercisesApi.controller.binding.ExerciseAddModel;
import com.dimitarrradev.exercisesApi.controller.binding.ImageUrlAddModel;
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
import com.dimitarrradev.exercisesApi.exercise.util.ExerciseModelAssembler;
import com.dimitarrradev.exercisesApi.exercise.util.ImageUrlFromModelMapper;
import com.dimitarrradev.exercisesApi.exercise.util.ImageUrlModelAssembler;
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
import org.springframework.hateoas.PagedModel;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceUnitTests {

    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private ImageUrlRepository imageUrlRepository;
    @Mock
    private ExerciseModelAssembler exerciseModelAssembler;
    @Mock
    private ImageUrlModelAssembler imageUrlModelAssembler;
    @Mock
    private ExerciseFromModelMapper exerciseFromModelMapper;
    @Mock
    private ImageUrlFromModelMapper imageUrlFromModelMapper;
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
        imageUrl.setExercise(exercise);
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
    void testAddExerciseCreatesNewExercise() {
        when(exerciseRepository.existsExerciseByName("test-exercise-1"))
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
                .build();

        when(exerciseRepository.save(exercise))
                .thenReturn(savedExercise);

        ExerciseModel expected = new ExerciseModel(
                savedExercise.getId(),
                savedExercise.getName(),
                savedExercise.getComplexity(),
                savedExercise.getDescription(),
                savedExercise.getMovementType(),
                savedExercise.getTargetBodyPart()
        );
        when(exerciseModelAssembler.toModel(savedExercise))
                .thenReturn(expected);

        ExerciseModel exerciseModel = exerciseService.addExercise(addModel);

        verify(exerciseRepository, times(1)).save(exercise);
        assertEquals(expected, exerciseModel);
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

        when(exerciseModelAssembler.toModel(exercise))
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

        CollectionModel<ExerciseModel> expectedExercises = CollectionModel.of(mapToExerciseModelList(exercises));

        when(exerciseModelAssembler.toCollectionModel(exercises))
                .thenReturn(expectedExercises);

        CollectionModel<ExerciseModel> exercisesViewByTargets = exerciseService.getExercisesForTargetBodyParts(List.of(randomTargetBodyPart));

        assertEquals(CollectionModel.of(expectedExercises), exercisesViewByTargets);
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

        when(exerciseRepository.findAll(pageable))
                .thenReturn(page);

        List<ExerciseModel> mappedExercises = mapToExerciseModelList(page.getContent());

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        when(pagedResourcesAssembler.toModel(page, exerciseModelAssembler))
                .thenReturn(PagedModel.of(mappedExercises, pageMetadata));


        assertEquals(PagedModel.of(mappedExercises, pageMetadata), exerciseService.getExercises(0, 10, "asc"));

    }

    @Test
    void testGetAllExercisesReturnsCorrectListOfExercises() {
        List<Exercise> exerciseList = generateExerciseList(5)
                .stream()
                .toList();

        List<ExerciseModel> mappedExercises = mapToExerciseModelList(exerciseList);

        when(exerciseRepository.findAll())
                .thenReturn(exerciseList);

        CollectionModel<ExerciseModel> collectionModel = CollectionModel.of(mappedExercises);

        when(exerciseModelAssembler.toCollectionModel(exerciseList)).thenReturn(collectionModel);

        assertThat(exerciseService.getAllExercises())
                .isEqualTo(collectionModel);
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseModelWithExerciseName() {
        List<Exercise> exerciseList = generateExerciseList(12).stream()
                .filter(exercise -> exercise.getName().contains("1"))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAllByNameContainingIgnoreCase(pageable, "1"))
                .thenReturn(new PageImpl<>(exerciseList));

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        when(pagedResourcesAssembler.toModel(exercisePage, exerciseModelAssembler))
                .thenReturn(PagedModel.of(exerciseModelList, pageMetadata));

        assertThat(exerciseService.findExercises("1", "all", "all", "all", 1, pageable.getPageSize(), "asc"))
                .isEqualTo(PagedModel.of(exerciseModelList, pageMetadata));
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseModelWithTargetBodyPart() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.BACK))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAllByTargetBodyPart(
                pageable,
                TargetBodyPart.BACK
        )).thenReturn(exercisePage);


        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        when(pagedResourcesAssembler.toModel(exercisePage, exerciseModelAssembler))
                .thenReturn(PagedModel.of(exerciseModelList, pageMetadata));


        assertThat(exerciseService.findExercises("", "back", "all", "", 1, pageable.getPageSize(), "desc"))
                .isEqualTo(PagedModel.of(exerciseModelList, pageMetadata));
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseModelWithTargetBodyPartAndComplexity() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.LEGS)
                                && exercise.getComplexity().equals(Complexity.EASY))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAllByTargetBodyPartAndComplexity(
                pageable,
                TargetBodyPart.LEGS,
                Complexity.EASY
        )).thenReturn(new PageImpl<>(exerciseList));


        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        when(pagedResourcesAssembler.toModel(exercisePage, exerciseModelAssembler))
                .thenReturn(PagedModel.of(exerciseModelList, pageMetadata));


        assertThat(exerciseService.findExercises("", "legs", "easy", "all", 1, pageable.getPageSize(), "desc"))
                .isEqualTo(PagedModel.of(exerciseModelList, pageMetadata));
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseFindViewModelWithTargetBodyPartAndMovementType() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getTargetBodyPart().equals(TargetBodyPart.LEGS)
                                && exercise.getMovementType().equals(MovementType.ISOLATION))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAllByTargetBodyPartAndMovementType(
                pageable,
                TargetBodyPart.LEGS,
                MovementType.ISOLATION
        )).thenReturn(new PageImpl<>(exerciseList));

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        when(pagedResourcesAssembler.toModel(exercisePage, exerciseModelAssembler))
                .thenReturn(PagedModel.of(exerciseModelList, pageMetadata));

        assertThat(exerciseService.findExercises("", "legs", "", "isolation", 1, pageable.getPageSize(), "desc"))
                .isEqualTo(PagedModel.of(exerciseModelList, pageMetadata));
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

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAllByTargetBodyPartAndComplexityAndMovementType(
                pageable,
                TargetBodyPart.LEGS,
                Complexity.EASY,
                MovementType.ISOLATION
        )).thenReturn(new PageImpl<>(exerciseList));

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        when(pagedResourcesAssembler.toModel(exercisePage, exerciseModelAssembler))
                .thenReturn(PagedModel.of(exerciseModelList, pageMetadata));

        assertThat(exerciseService.findExercises("", "legs", "easy", "isolation", 1, pageable.getPageSize(), "desc"))
                .isEqualTo(PagedModel.of(exerciseModelList, pageMetadata));
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

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAllByComplexityAndMovementType(
                pageable,
                Complexity.EASY,
                MovementType.ISOLATION
        )).thenReturn(new PageImpl<>(exerciseList));

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        when(pagedResourcesAssembler.toModel(exercisePage, exerciseModelAssembler))
                .thenReturn(PagedModel.of(exerciseModelList, pageMetadata));

        assertThat(exerciseService.findExercises("", "all", "easy", "isolation", 1, pageable.getPageSize(), "asc"))
                .isEqualTo(PagedModel.of(exerciseModelList, pageMetadata));
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseFindViewModelWithComplexity() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getComplexity().equals(Complexity.EASY)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAllByComplexity(
                pageable,
                Complexity.EASY
        )).thenReturn(new PageImpl<>(exerciseList));

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        when(pagedResourcesAssembler.toModel(exercisePage, exerciseModelAssembler))
                .thenReturn(PagedModel.of(exerciseModelList, pageMetadata));

        assertThat(exerciseService.findExercises("", "", "easy", "", 1, pageable.getPageSize(), "asc"))
                .isEqualTo(PagedModel.of(exerciseModelList, pageMetadata));
    }

    @Test
    void testFindExercisesReturnsCorrectCollectionModelOfExerciseFindViewModelWithMovementTypePage() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .filter(exercise ->
                        exercise.getMovementType().equals(MovementType.ISOLATION)
                )
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        PageImpl<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAllByMovementType(
                pageable,
                MovementType.ISOLATION
        )).thenReturn(new PageImpl<>(exerciseList));

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        when(pagedResourcesAssembler.toModel(exercisePage, exerciseModelAssembler))
                .thenReturn(PagedModel.of(exerciseModelList, pageMetadata));

        assertThat(exerciseService.findExercises("", "", "", "isolation", 1, pageable.getPageSize(), "asc"))
                .isEqualTo(PagedModel.of(exerciseModelList, pageMetadata));
    }

    @Test
    void testFindExercisesPageReturnsCorrectCollectionModelOfExerciseFindViewModelWithNoFiltersPage() {
        List<Exercise> exerciseList = generateExerciseList(10).stream()
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();

        List<ExerciseModel> exerciseModelList = mapToExerciseModelList(exerciseList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        PageImpl<Exercise> exercisePage = new PageImpl<>(exerciseList);

        when(exerciseRepository.findAll(
                pageable
        )).thenReturn(exercisePage);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                exercisePage.getSize(),
                exercisePage.getNumber(),
                exercisePage.getTotalElements(),
                exercisePage.getTotalPages()
        );

        when(pagedResourcesAssembler.toModel(exercisePage, exerciseModelAssembler))
                .thenReturn(PagedModel.of(exerciseModelList, pageMetadata));

        assertThat(exerciseService.findExercises("", "", "", "", 1, pageable.getPageSize(), "asc"))
                .isEqualTo(PagedModel.of(exerciseModelList, pageMetadata));
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
                                exercise.getId()
                        )
                ).toList();


        when(imageUrlModelAssembler.toCollectionModel(exercise.getImageURLs()))
                .thenReturn(CollectionModel.of(urlModelStream));

        assertEquals(CollectionModel.of(urlModelStream),
                exerciseService.getImages(exercise.getId()));
    }

    @Test
    void testAddImageThrowsWhenExerciseIsNotFound() {
        when(exerciseRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.addImage(0L, new ImageUrlAddModel("https://some-url.com"))
        );
    }

    @Test
    void testAddImageCreatesNewImageAndSavesItWhenExerciseExists() {
        when(exerciseRepository.findById(exercise.getId()))
                .thenReturn(Optional.of(exercise));

        ImageUrlAddModel addModel = new ImageUrlAddModel("https://some-image.com");

        ImageUrl imageUrl = new ImageUrl(null, addModel.url(), null);

        when(imageUrlFromModelMapper.fromImageUrlAddModel(addModel))
                .thenReturn(imageUrl);

        ImageUrl savedImageUrl = new ImageUrl(2L, addModel.url(), exercise);

        when(imageUrlRepository.save(imageUrl))
                .thenReturn(savedImageUrl);

        ImageUrlModel expectedModel = new ImageUrlModel(
                savedImageUrl.getId(),
                savedImageUrl.getUrl(),
                savedImageUrl.getExercise().getId());

        when(imageUrlModelAssembler.toModel(savedImageUrl))
                .thenReturn(expectedModel);

        ImageUrlModel imageUrlModel = exerciseService.addImage(exercise.getId(), addModel);

        assertEquals(expectedModel, imageUrlModel);
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

        ImageUrlModel expected = new ImageUrlModel(imageUrl.getId(), imageUrl.getUrl(), exercise.getId());

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

        exerciseService.deleteImage(exercise.getId(), imageUrl.getId());

        verify(imageUrlRepository, times(1)).delete(imageUrl);
    }

    private static List<ExerciseModel> mapToExerciseModelList(List<Exercise> exerciseList) {
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
