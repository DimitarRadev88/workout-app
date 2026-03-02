package com.dimitarrradev.exercisesApi.exercise.service;

import com.dimitarrradev.exercisesApi.controller.binding.ExerciseAddModel;
import com.dimitarrradev.exercisesApi.controller.binding.ExerciseEditModel;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ImageUrlRepository imageUrlRepository;
    private final ExerciseModelAssembler exerciseModelAssembler;
    private final ImageUrlModelAssembler imageUrlModelAssembler;
    private final ExerciseFromModelMapper exerciseFromModelMapper;
    private final ImageUrlFromModelMapper imageUrlFromModelMapper;
    private final PagedResourcesAssembler<Exercise> resourcesAssembler;

    public ExerciseModel addExercise(ExerciseAddModel exerciseAddModel) {
        if (exerciseRepository.existsExerciseByNameAndIsDeletedFalse(exerciseAddModel.name())) {
            throw new ExerciseAlreadyExistsException("Exercise with name " + exerciseAddModel.name() + " already exists!");
        }

        Exercise exercise = exerciseFromModelMapper.fromExerciseAddModel(exerciseAddModel);

        exercise.setIsDeleted(Boolean.FALSE);

        return exerciseModelAssembler.toModel(exerciseRepository.save(exercise));
    }

    public ExerciseModel deleteExercise(Long id) {
        Exercise toDelete = exerciseRepository
                .findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found!"));

        toDelete.setIsDeleted(Boolean.TRUE);

        exerciseRepository.saveAndFlush(toDelete);

        return exerciseModelAssembler.toModel(toDelete);
    }

    public PagedModel<ExerciseModel> searchExercises(String name, TargetBodyPart target, Complexity complexity, MovementType movement, int page, int size, String orderBy) {
        Sort sort = orderBy.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Exercise> exercisesPage = getExercisePage(name, target, complexity, movement, pageable);

        return resourcesAssembler.toModel(exercisesPage, exerciseModelAssembler);
    }

    public ExerciseModel getExerciseModel(Long id) {
        log.info("Getting exercise view with id: {}", id);
        return exerciseRepository
                .findById(id).map(exerciseModelAssembler::toModel)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));
    }

    public ExerciseModel editExercise(Long id, ExerciseEditModel editModel) {
        Exercise exercise = exerciseRepository
                .findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));

        exercise.setName(editModel.name());
        exercise.setDescription(editModel.description());
        exercise.setComplexity(editModel.complexity());
        exercise.setTargetBodyPart(editModel.bodyPart());
        exercise.setMovementType(editModel.movement());

        return exerciseModelAssembler.toModel(exerciseRepository.saveAndFlush(exercise));
    }

    public CollectionModel<ExerciseModel> getExercisesForTargetBodyParts(List<TargetBodyPart> targetBodyParts) {
        return exerciseModelAssembler.toCollectionModel(exerciseRepository
                .findAllByTargetBodyPartIsInAndIsDeletedFalse(targetBodyParts));
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

        return imageUrlModelAssembler.toCollectionModel(
                imageUrlRepository.findByExercise_Id(id)
        );
    }

    public ImageUrlModel addImage(Long id, ImageUrlAddModel addModel) {
        Exercise exercise = exerciseRepository
                .findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found!"));

        ImageUrl image = imageUrlFromModelMapper.fromImageUrlAddModel(addModel);

        image.setExercise(exercise);

        return imageUrlModelAssembler.toModel(imageUrlRepository.save(image));
    }

    public ImageUrlModel getImage(Long id, Long imageId) {
        ImageUrl imageUrl = imageUrlRepository.findByIdAndExercise_id(imageId, id)
                .orElseThrow(() -> new ImageNotFoundException("Image for exercise with id " + imageId + " does not exist!"));

        return imageUrlModelAssembler.toModel(imageUrl);
    }

    public void deleteImage(Long id, Long imageId) {
        ImageUrl imageUrl = imageUrlRepository.findByIdAndExercise_id(imageId, id)
                .orElseThrow(() -> new ImageNotFoundException("Image for exercise with id " + imageId + " does not exist!"));

        imageUrlRepository.delete(imageUrl);
    }

    private Page<Exercise> getExercisePage(String name, TargetBodyPart target, Complexity complexity, MovementType movement, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return getExercisePage(target, complexity, movement, pageable);
        }

        return exerciseRepository
                .findAllByNameContainingIgnoreCaseAndIsDeletedFalse(pageable, name);
    }

    private Page<Exercise> getExercisePage(TargetBodyPart target, Complexity complexity, MovementType movement, Pageable pageable) {
        if (target.equals(TargetBodyPart.ALL)) {
            return getExercisePage(complexity, movement, pageable);
        } else if (complexity.equals(Complexity.ALL)) {
            return getExercisePage(target, movement, pageable);
        } else if (movement.equals(MovementType.ALL)) {
            return getExercisePage(target, complexity, pageable);
        }

        return exerciseRepository
                .findAllByTargetBodyPartAndComplexityAndMovementTypeAndIsDeletedFalse(
                        pageable,
                        target,
                        complexity,
                        movement
                );
    }

    private Page<Exercise> getExercisePage(TargetBodyPart target, MovementType movement, Pageable pageable) {
        if (target.equals(TargetBodyPart.ALL)) {
            return getExercisePage(movement, pageable);
        } else if (movement.equals(MovementType.ALL)) {
            return getExercisePage(target, pageable);
        }

        return exerciseRepository
                .findAllByTargetBodyPartAndMovementTypeAndIsDeletedFalse(
                        pageable,
                        target,
                        movement
                );
    }

    private Page<Exercise> getExercisePage(TargetBodyPart target, Complexity complexity, Pageable pageable) {
        if (target.equals(TargetBodyPart.ALL)) {
            return getExercisePage(complexity, pageable);
        } else if (complexity.equals(Complexity.ALL)) {
            return getExercisePage(target, pageable);
        }

        return exerciseRepository
                .findAllByTargetBodyPartAndComplexityAndIsDeletedFalse(
                        pageable,
                        target,
                        complexity
                );
    }

    private Page<Exercise> getExercisePage(Complexity complexity, MovementType movement, Pageable pageable) {
        if (complexity.equals(Complexity.ALL)) {
            return getExercisePage(movement, pageable);
        } else if (movement.equals(MovementType.ALL)) {
            return getExercisePage(complexity, pageable);
        }

        return exerciseRepository
                .findAllByComplexityAndMovementTypeAndIsDeletedFalse(
                        pageable,
                        complexity,
                        movement
                );
    }

    private Page<Exercise> getExercisePage(TargetBodyPart target, Pageable pageable) {
        if (target.equals(TargetBodyPart.ALL)) {
            return getExercisePage(pageable);
        }

        return exerciseRepository
                .findAllByTargetBodyPartAndIsDeletedFalse(
                        pageable,
                        target
                );
    }

    private Page<Exercise> getExercisePage(Complexity complexity, Pageable pageable) {
        if (complexity.equals(Complexity.ALL)) {
            return getExercisePage(pageable);
        }

        return exerciseRepository
                .findAllByComplexityAndIsDeletedFalse(
                        pageable,
                        complexity
                );
    }

    private Page<Exercise> getExercisePage(MovementType movement, Pageable pageable) {
        if (movement.equals(MovementType.ALL)) {
            return getExercisePage(pageable);
        }

        return exerciseRepository
                .findAllByMovementTypeAndIsDeletedFalse(
                        pageable,
                        movement
                );
    }

    private Page<Exercise> getExercisePage(Pageable pageable) {
        return exerciseRepository
                .findAllByIsDeletedFalse(pageable);
    }

}