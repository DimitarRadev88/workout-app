package com.dimitarrradev.exercisesApi.exercise.service;

import com.dimitarrradev.exercisesApi.exercise.Exercise;
import com.dimitarrradev.exercisesApi.exercise.dao.ExerciseRepository;
import com.dimitarrradev.exercisesApi.exercise.dto.ExerciseModel;
import com.dimitarrradev.exercisesApi.exercise.dto.PageInformation;
import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.util.error.message.exception.ExerciseAlreadyExistsException;
import com.dimitarrradev.exercisesApi.util.error.message.exception.ExerciseNotFoundException;
import com.dimitarrradev.exercisesApi.util.mapping.ExerciseToViewModelMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseToViewModelMapper mapperTo;

    public Long addExerciseForReview(String name, String description, String bodyPart, String addedBy, String complexity, String movement) {
        if (exerciseRepository.existsExerciseByName(name)) {
            throw new ExerciseAlreadyExistsException("Exercise with name " + name + "  already exists");
        }

        Exercise exercise = Exercise.builder()
                .name(name)
                .description(description)
                .targetBodyPart(TargetBodyPart.valueOf(bodyPart.toUpperCase()))
                .addedBy(addedBy)
                .complexity(Complexity.valueOf(complexity.toUpperCase()))
                .movementType(MovementType.valueOf(movement.toUpperCase()))
                .approved(Boolean.FALSE)
                .build();

        return exerciseRepository.save(exercise).getId();
    }

    public long getExercisesForReviewCount() {
        return exerciseRepository.countAllByApprovedFalse();
    }

    public Page<ExerciseModel> getExercisesForReviewPage(int pageNumber, int pageSize, String orderBy) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderBy), "name");

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        return exerciseRepository
                .findAllByApprovedIsAndNameContainingIgnoreCase(pageable, false, "")
                .map(mapperTo::toExerciseModel);
    }

    public void approveExercise(Long id) {
        exerciseRepository.findById(id).map(exercise -> {
            exercise.setApproved(true);
            exerciseRepository.save(exercise);
            return exercise;
        }).orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));
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

    public Page<ExerciseModel> findActiveExercisesPage(String name, String target, String complexity, String movement, int page, int size, String orderBy) {
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
            exercisesPage = getExerciseFindViewModelPageByTargetBodyPartComplexityMovementTypeAndActiveTrue(pageable, targetBodyPart, complexityEnum, movementType);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL) && !complexityEnum.equals(Complexity.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByTargetBodyPartComplexityAndActiveTrue(pageable, targetBodyPart, complexityEnum);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL) && !movementType.equals(MovementType.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByTargetBodyPartMovementTypeAndActiveTrue(pageable, targetBodyPart, movementType);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByTargetBodyPartAndActiveTrue(pageable, targetBodyPart);
        } else if (!complexityEnum.equals(Complexity.ALL) && !movementType.equals(MovementType.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByComplexityMovementTypeAndActiveTrue(pageable, complexityEnum, movementType);
        } else if (!complexityEnum.equals(Complexity.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByComplexityAndActiveTrue(pageable, complexityEnum);
        } else if (!movementType.equals(MovementType.ALL)) {
            exercisesPage = getExerciseFindViewModelPageByMovementTypeAndActiveTrue(pageable, movementType);
        } else {
            exercisesPage = getExerciseFindViewModelPageByActiveTrue(pageable);
        }

        return exercisesPage.map(mapperTo::toExerciseModel);
    }

    private Page<Exercise> getExerciseFindViewModelPageByTargetBodyPartAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart) {
        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPart(
                        pageable,
                        targetBodyPart
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByActiveTrue(Pageable pageable) {
        return exerciseRepository
                .findAllByApprovedTrue(
                        pageable
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByMovementTypeAndActiveTrue(Pageable pageable, MovementType movementType) {
        return exerciseRepository
                .findAllByApprovedTrueAndMovementType(
                        pageable,
                        movementType
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByComplexityAndActiveTrue(Pageable pageable, Complexity complexity) {
        return exerciseRepository
                .findAllByApprovedTrueAndComplexity(
                        pageable,
                        complexity
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByComplexityMovementTypeAndActiveTrue(Pageable pageable, Complexity complexity, MovementType movementType) {
        return exerciseRepository
                .findAllByApprovedTrueAndComplexityAndMovementType(
                        pageable,
                        complexity,
                        movementType
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByTargetBodyPartMovementTypeAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, MovementType movementType) {
        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPartAndMovementType(
                        pageable,
                        targetBodyPart,
                        movementType
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByTargetBodyPartComplexityAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity) {
        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPartAndComplexity(
                        pageable,
                        targetBodyPart,
                        complexity
                );
    }

    private Page<Exercise> getExerciseFindViewModelPageByTargetBodyPartComplexityMovementTypeAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity, MovementType movementType) {
        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPartAndComplexityAndMovementType(
                        pageable,
                        targetBodyPart,
                        complexity,
                        movementType
                );
    }


    private Page<Exercise> getActiveExercisesWithNameContaining(String exerciseName, Pageable pageable) {
        return exerciseRepository
                .findAllByApprovedTrueAndNameContainingIgnoreCase(pageable, exerciseName);
    }

    @Transactional
    public ExerciseModel getExerciseModel(Long id) {
        log.info("Getting exercise view with id: {}", id);
        Exercise exercise = exerciseRepository
                .findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));

        return mapperTo.toExerciseModel(exercise);
    }

    @Transactional
    public void editExercise(Long id, String name, String description, Boolean approved) {
        Exercise exercise = exerciseRepository
                .findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));

        if (name != null && !name.trim().isEmpty()) {
            exercise.setName(name);
        }

        if (description != null && !description.trim().isEmpty()) {
            exercise.setDescription(description);
        }

        if (approved != null) {
            exercise.setApproved(approved);
        }

        exerciseRepository.save(exercise);
    }

    public List<ExerciseModel> getExercisesForTargetBodyParts(List<TargetBodyPart> targetBodyParts) {
        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPartIsIn(targetBodyParts)
                .stream()
                .map(mapperTo::toExerciseModel)
                .toList();
    }

    public Exercise getExerciseEntity(Long id) {
        return exerciseRepository
                .findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));
    }

    public <T> PageInformation getPageInfo(Page<T> page) {
        long elementsShownFrom = page.getTotalElements() == 0 ? 0 : Math.min(page.getTotalElements(), (long) (page.getNumber() + 1) * page.getSize());
        long elementsShownTo = (page.getNumber() + 1) < page.getTotalPages() ? (long) (page.getNumber() + 1) * page.getSize() : page.getTotalElements();
        List<Integer> pageSizes = List.of(5, 10, 25, 50);

        return new PageInformation(
                String.format("Showing %d to %d of %d exercises",
                        elementsShownFrom,
                        elementsShownTo,
                        page.getTotalElements()
                ),
                pageSizes
        );
    }

    public List<ExerciseModel> getAllActiveExercises() {
        return exerciseRepository
                .findAllByApprovedTrue()
                .stream()
                .map(mapperTo::toExerciseModel)
                .toList();
    }
}