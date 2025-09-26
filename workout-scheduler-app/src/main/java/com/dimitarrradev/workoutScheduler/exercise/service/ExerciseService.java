package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.dao.ExerciseRepository;
import com.dimitarrradev.workoutScheduler.exercise.dao.ImageUrlRepository;
import com.dimitarrradev.workoutScheduler.exercise.dto.*;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.util.mapping.ExerciseFromBindingModelMapper;
import com.dimitarrradev.workoutScheduler.util.mapping.ExerciseToViewModelMapper;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseFindBindingModel;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    private static final Logger log = LoggerFactory.getLogger(ExerciseService.class);
    private final ExerciseRepository exerciseRepository;
    private final ImageUrlRepository imageUrlRepository;
    private final ExerciseFromBindingModelMapper mapperFrom;
    private final ExerciseToViewModelMapper mapperTo;

    public ExerciseService(ExerciseRepository exerciseRepository, ImageUrlRepository imageUrlRepository, ExerciseFromBindingModelMapper mapperFrom, ExerciseToViewModelMapper mapperTo) {
        this.exerciseRepository = exerciseRepository;
        this.imageUrlRepository = imageUrlRepository;
        this.mapperFrom = mapperFrom;
        this.mapperTo = mapperTo;
    }

    public void addExerciseForReview(ExerciseAddBindingModel exerciseAdd) {
        if (exerciseRepository.existsExerciseByName(exerciseAdd.exerciseName())) {
            throw new IllegalArgumentException("Exercise already exists");
        }

        Exercise exercise = mapperFrom.fromExerciseAddBindingModel(exerciseAdd);

        exerciseRepository.save(exercise);
    }

    public long getExercisesForReviewCount() {
        return exerciseRepository.countAllByApprovedFalse();
    }

    public Page<ExerciseForReviewViewModel> getExercisesForReviewPage(int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return exerciseRepository
                .findAllByApprovedIsAndNameContainingIgnoreCase(pageable, false, "")
                .map(mapperTo::toExerciseForReviewViewModel);
    }

    public void approveExercise(Long id) {
        Optional<Exercise> exerciseOptional = exerciseRepository.findById(id);

        if (exerciseOptional.isEmpty()) {
            throw new IllegalArgumentException("Exercise not found");
        }

        exerciseOptional.ifPresent(exercise -> exerciseRepository.save(
                mapperFrom.fromExerciseToApprovedExercise(exercise)
        ));

    }

    public void deleteExercise(Long id) {
        Optional<Exercise> exerciseOptional = exerciseRepository.findById(id);

        if (exerciseOptional.isEmpty()) {
            throw new IllegalArgumentException("Exercise not found");
        }

        exerciseOptional.ifPresent(exerciseRepository::delete);
    }

    public Page<ExerciseFindViewModel> findActiveExercisesPage(ExerciseFindBindingModel exerciseFind, int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        if (exerciseFind.name() != null && !exerciseFind.name().isEmpty()) {
            return getActiveExercisesWithNameContaining(exerciseFind.name(), pageable);
        }

        TargetBodyPart targetBodyPart = exerciseFind.targetBodyPart() == null ? TargetBodyPart.ALL : exerciseFind.targetBodyPart();
        Complexity complexity = exerciseFind.complexity() == null ? Complexity.ALL : exerciseFind.complexity();
        MovementType movementType = exerciseFind.movementType() == null ? MovementType.ALL : exerciseFind.movementType();

        if (!targetBodyPart.equals(TargetBodyPart.ALL) && !complexity.equals(Complexity.ALL) && !movementType.equals(MovementType.ALL)) {
            return getExerciseFindViewModelPageByTargetBodyPartComplexityMovementTypeAndActiveTrue(pageable, targetBodyPart, complexity, movementType);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL) && !complexity.equals(Complexity.ALL)) {
            return getExerciseFindViewModelPageByTargetBodyPartComplexityAndActiveTrue(pageable, targetBodyPart, complexity);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL) && !movementType.equals(MovementType.ALL)) {
            return getExerciseFindViewModelPageByTargetBodyPartMovementTypeAndActiveTrue(pageable, targetBodyPart, movementType);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL)) {
            return getExerciseFindViewModelPageByTargetBodyPartAndActiveTrue(pageable, targetBodyPart);
        } else if (!complexity.equals(Complexity.ALL) && !movementType.equals(MovementType.ALL)) {
            return getExerciseFindViewModelPageByComplexityMovementTypeAndActiveTrue(pageable, complexity, movementType);
        } else if (!complexity.equals(Complexity.ALL)) {
            return getExerciseFindViewModelPageByComplexityAndActiveTrue(pageable, complexity);
        } else if (!movementType.equals(MovementType.ALL)) {
            return getExerciseFindViewModelPageByMovementTypeAndActiveTrue(pageable, movementType);
        }

        return getExerciseFindViewModelPageByActiveTrue(pageable);
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByTargetBodyPartAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart) {
        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPart(
                        pageable,
                        targetBodyPart
                ).map(mapperTo::toExerciseFindViewModel);
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByActiveTrue(Pageable pageable) {
        return exerciseRepository
                .findAllByApprovedTrue(
                        pageable
                ).map(mapperTo::toExerciseFindViewModel);
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByMovementTypeAndActiveTrue(Pageable pageable, MovementType movementType) {
        return exerciseRepository
                .findAllByApprovedTrueAndMovementType(
                        pageable,
                        movementType
                ).map(mapperTo::toExerciseFindViewModel);
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByComplexityAndActiveTrue(Pageable pageable, Complexity complexity) {
        return exerciseRepository
                .findAllByApprovedTrueAndComplexity(
                        pageable,
                        complexity
                ).map(mapperTo::toExerciseFindViewModel);
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByComplexityMovementTypeAndActiveTrue(Pageable pageable, Complexity complexity, MovementType movementType) {
        return exerciseRepository
                .findAllByApprovedTrueAndComplexityAndMovementType(
                        pageable,
                        complexity,
                        movementType
                ).map(mapperTo::toExerciseFindViewModel);
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByTargetBodyPartMovementTypeAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, MovementType movementType) {
        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPartAndMovementType(
                        pageable,
                        targetBodyPart,
                        movementType
                ).map(mapperTo::toExerciseFindViewModel);
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByTargetBodyPartComplexityAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity) {
        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPartAndComplexity(
                        pageable,
                        targetBodyPart,
                        complexity
                ).map(mapperTo::toExerciseFindViewModel);
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByTargetBodyPartComplexityMovementTypeAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity, MovementType movementType) {
        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPartAndComplexityAndMovementType(
                        pageable,
                        targetBodyPart,
                        complexity,
                        movementType
                ).map(mapperTo::toExerciseFindViewModel);
    }


    private Page<ExerciseFindViewModel> getActiveExercisesWithNameContaining(String exerciseName, Pageable pageable) {
        return exerciseRepository
                .findAllByApprovedTrueAndNameContainingIgnoreCase(pageable, exerciseName)
                .map(mapperTo::toExerciseFindViewModel);
    }

    @Transactional
    public ExerciseViewModel getExerciseView(Long id) {
        Exercise exercise = exerciseRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        return mapperTo.toExerciseViewModel(exercise);
    }

    @Transactional
    public void editExercise(ExerciseEditBindingModel exerciseEdit) {
        Exercise exercise = exerciseRepository
                .findById(exerciseEdit.id())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        exerciseRepository.save(mapperFrom.fromExerciseEditBindingModel(exercise, exerciseEdit));
    }

    public void deleteImageUrl(long id) {
        imageUrlRepository.deleteById(id);
    }

    public ExerciseEditBindingModel getExerciseEditBindingModel(long id) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        return mapperTo.toExerciseEditBindingModel(exercise);
    }

    public List<ImageUrlViewModel> getExerciseImages(long exerciseId) {
        List<ImageUrlViewModel> result = imageUrlRepository.findByExercise_Id(exerciseId).stream()
                .map(imageUrl -> new ImageUrlViewModel(imageUrl.getId(), imageUrl.getUrl()))
                .toList();

        return result.isEmpty() ? new ArrayList<>() : result;

    }

    public List<ExerciseNameAndIdViewModel> getExercisesForTargetBodyParts(List<TargetBodyPart> targetBodyParts) {
        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPartIsIn(targetBodyParts)
                .stream()
                .map(mapperTo::toExerciseNameAndIdViewModel)
                .toList();
    }

    public Exercise getExercise(Long id) {
        return exerciseRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
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

    public List<ExerciseFindViewModel> getAllActiveExercises() {
        return exerciseRepository
                .findAllByApprovedTrue()
                .stream()
                .map(mapperTo::toExerciseFindViewModel)
                .toList();
    }
}