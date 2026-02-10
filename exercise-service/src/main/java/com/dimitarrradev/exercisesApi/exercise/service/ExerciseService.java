package com.dimitarrradev.exercisesApi.exercise.service;

import com.dimitarrradev.exercisesApi.exercise.dao.ExerciseRepository;
import com.dimitarrradev.exercisesApi.exercise.dao.ImageUrlRepository;
import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.exercise.model.*;
import com.dimitarrradev.exercisesApi.util.error.message.exception.ExerciseAlreadyExistsException;
import com.dimitarrradev.exercisesApi.util.error.message.exception.ExerciseNotFoundException;
import com.dimitarrradev.exercisesApi.util.error.message.exception.ImageNotFoundException;
import com.dimitarrradev.exercisesApi.web.ExerciseController;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ImageUrlRepository imageUrlRepository;
    private final ExerciseModelAssembler exerciseModelAssembler;
    private final ImageUrlModelAssembler imageUrlModelAssembler;

    public Long addExercise(String name, String description, String bodyPart, String complexity, String movement) {
        if (exerciseRepository.existsExerciseByName(name)) {
            throw new ExerciseAlreadyExistsException("Exercise with name " + name + "  already exists");
        }

        Exercise exercise = Exercise.builder()
                .name(name)
                .description(description)
                .targetBodyPart(TargetBodyPart.valueOf(bodyPart.toUpperCase()))
                .complexity(Complexity.valueOf(complexity.toUpperCase()))
                .movementType(MovementType.valueOf(movement.toUpperCase()))
                .build();

        return exerciseRepository.save(exercise).getId();
    }

    public CollectionModel<ExerciseModel> getExercises(int pageNumber, int pageSize, String orderBy) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderBy), "name");

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Exercise> exercisesPage = exerciseRepository.findAllBy(pageable);

        var result = exerciseModelAssembler.toCollectionModel(exercisesPage);

        result.add(linkTo(methodOn(ExerciseController.class).getExercisesPage(pageNumber, pageSize, orderBy)).withSelfRel());
        result.add(linkTo(methodOn(ExerciseController.class).getExercisesPage(Math.min(exercisesPage.getTotalPages() - 1, pageNumber + 1), pageSize, orderBy)).withRel("next"));
        result.add(linkTo(methodOn(ExerciseController.class).getExercisesPage(Math.max(0, pageNumber - 1), pageSize, orderBy)).withRel("prev"));
        result.add(linkTo(methodOn(ExerciseController.class).getExercisesPage(0, pageSize, orderBy)).withRel("first"));
        result.add(linkTo(methodOn(ExerciseController.class).getExercisesPage(exercisesPage.getTotalPages() - 1, pageSize, orderBy)).withRel("last"));

        return result;
    }

    public void deleteExercise(Long id) {
        exerciseRepository.findById(id)
                .ifPresentOrElse(
                        exerciseRepository::delete,
                        () -> {
                            throw new ExerciseNotFoundException("Exercise not found");
                        }
                );
    }

    public CollectionModel<ExerciseModel> findExercises(String name, String target, String complexity, String movement, int page, int size, String orderBy) {
        Sort sort = orderBy.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        TargetBodyPart targetBodyPart = target == null || target.trim().isEmpty() ? TargetBodyPart.ALL : TargetBodyPart.valueOf(target.toUpperCase());
        Complexity complexityEnum = complexity == null || complexity.trim().isEmpty() ? Complexity.ALL : Complexity.valueOf(complexity.toUpperCase());
        MovementType movementType = movement == null || movement.trim().isEmpty() ? MovementType.ALL : MovementType.valueOf(movement.toUpperCase());

        Page<Exercise> exercisesPage = null;

        if (name != null && !name.trim().isEmpty()) {
            exercisesPage = getActiveExercisesWithNameContaining(name, pageable);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL) && !complexityEnum.equals(Complexity.ALL) && !movementType.equals(MovementType.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByTargetBodyPartComplexityMovementType(pageable, targetBodyPart, complexityEnum, movementType);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL) && !complexityEnum.equals(Complexity.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByTargetBodyPartComplexity(pageable, targetBodyPart, complexityEnum);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL) && !movementType.equals(MovementType.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByTargetBodyPartMovementType(pageable, targetBodyPart, movementType);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByTargetBodyPart(pageable, targetBodyPart);
        } else if (!complexityEnum.equals(Complexity.ALL) && !movementType.equals(MovementType.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByComplexityMovementType(pageable, complexityEnum, movementType);
        } else if (!complexityEnum.equals(Complexity.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByComplexity(pageable, complexityEnum);
        } else if (!movementType.equals(MovementType.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByMovementType(pageable, movementType);
        } else {
            exercisesPage = getExerciseFindViewModelPage(pageable);
        }

        return exerciseModelAssembler.toCollectionModel(exercisesPage);
    }

    private Page<Exercise> getExerciseFindViewModelPageByTargetBodyPart(Pageable pageable, TargetBodyPart targetBodyPart) {
        return exerciseRepository
                .findAllByTargetBodyPart(
                        pageable,
                        targetBodyPart
                );
    }

    private Page<Exercise> getExerciseFindViewModelPage(Pageable pageable) {
        return exerciseRepository
                .findAll(
                        pageable
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByMovementType(Pageable pageable, MovementType movementType) {
        return exerciseRepository
                .findAllByMovementType(
                        pageable,
                        movementType
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByComplexity(Pageable pageable, Complexity complexity) {
        return exerciseRepository
                .findAllByComplexity(
                        pageable,
                        complexity
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByComplexityMovementType(Pageable pageable, Complexity complexity, MovementType movementType) {
        return exerciseRepository
                .findAllByComplexityAndMovementType(
                        pageable,
                        complexity,
                        movementType
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByTargetBodyPartMovementType(Pageable pageable, TargetBodyPart targetBodyPart, MovementType movementType) {
        return exerciseRepository
                .findAllByTargetBodyPartAndMovementType(
                        pageable,
                        targetBodyPart,
                        movementType
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByTargetBodyPartComplexity(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity) {
        return exerciseRepository
                .findAllByTargetBodyPartAndComplexity(
                        pageable,
                        targetBodyPart,
                        complexity
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByTargetBodyPartComplexityMovementType(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity, MovementType movementType) {
        return exerciseRepository
                .findAllByTargetBodyPartAndComplexityAndMovementType(
                        pageable,
                        targetBodyPart,
                        complexity,
                        movementType
                );
    }

    private Page<Exercise> getActiveExercisesWithNameContaining(String exerciseName, Pageable pageable) {
        return exerciseRepository
                .findAllByNameContainingIgnoreCase(pageable, exerciseName);
    }

    @Transactional
    public ExerciseModel getExerciseModel(Long id) {
        log.info("Getting exercise view with id: {}", id);
        return exerciseRepository
                .findById(id).map(exerciseModelAssembler::toModel)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));
    }

    @Transactional
    public void editExercise(Long id, String name, String description) {
        Exercise exercise = exerciseRepository
                .findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));

        if (name != null && !name.trim().isEmpty()) {
            exercise.setName(name);
        }

        if (description != null && !description.trim().isEmpty()) {
            exercise.setDescription(description);
        }

        exerciseRepository.save(exercise);
    }

    public List<ExerciseModel> getExercisesForTargetBodyParts(List<TargetBodyPart> targetBodyParts) {
        return exerciseRepository
                .findAllByTargetBodyPartIsIn(targetBodyParts)
                .stream()
                .map(exerciseModelAssembler::toModel)
                .toList();
    }

    public Exercise getExerciseEntity(Long id) {
        return exerciseRepository
                .findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found!"));
    }

    public CollectionModel<ExerciseModel> getAllExercises() {
        return exerciseModelAssembler.toCollectionModel(exerciseRepository
                .findAll()
                .stream()
                .toList());
    }

    public CollectionModel<ImageUrlModel> getImages(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ExerciseNotFoundException("Exercise not found!");
        }

        return imageUrlModelAssembler.toCollectionModel(imageUrlRepository.findByExercise_Id(id));
    }

    public Long addImage(Long id, String url) {
        Exercise exercise = exerciseRepository
                .findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found!"));


        if (isValidUrl(url)) {
            ImageUrl image = new ImageUrl(null, url, exercise);
            exercise.getImageURLs().add(image);
            exerciseRepository.save(exercise);
            ImageUrl saved = imageUrlRepository.save(image);
            return saved.getId();
        }

        return null;
    }

    public ImageUrlModel getImage(Long id, Long imageId) {
        ImageUrl imageUrl = imageUrlRepository.findByIdAndExercise_id(imageId, id);

        if (imageUrl == null) {
            throw new ImageNotFoundException("Image for this exercise with id " + imageId + " does not exist!");
        }

        return imageUrlModelAssembler.toModel(imageUrl);
    }

    public void deleteImage(Long id, Long imageId) {
        ImageUrl imageUrl = imageUrlRepository.findByIdAndExercise_id(imageId, id);

        imageUrlRepository.delete(imageUrl);
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("https://");
    }

}