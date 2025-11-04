package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.config.rest.ExerciseRestClient;
import com.dimitarrradev.workoutScheduler.errors.exception.ExerciseAlreadyExistsException;
import com.dimitarrradev.workoutScheduler.exercise.dto.PageInformation;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseFindBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.view.*;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExerciseService {

    private final ExerciseRestClient restClient;

    public ExerciseService(ExerciseRestClient restClient) {
        this.restClient = restClient;
    }

    public String addExerciseForReview(ExerciseAddBindingModel exerciseAdd) {
        ResponseEntity<String> entity = restClient.addExercise(exerciseAdd);

        if (entity.getStatusCode().equals(HttpStatus.CONFLICT)) {
            throw new ExerciseAlreadyExistsException(entity.getBody());
        }

        log.info("Exercise added for review: {}", entity.getHeaders().getLocation());

        return entity.getBody();
    }

    public long getExercisesForReviewCount() {
        return restClient.getExercisesForReviewCount().getBody();
    }


    public Page<ExerciseForReviewViewModel> getExercisesForReviewPage(int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

//        return exerciseRepository
//                .findAllByApprovedIsAndNameContainingIgnoreCase(pageable, false, "")
//                .map(mapperTo::toExerciseForReviewViewModel);
        throw new IllegalStateException("TODO");

    }

    public void approveExercise(Long id) {
//        exerciseRepository.findById(id).map(exercise -> {
//            exercise.setApproved(true);
//            exerciseRepository.save(exercise);
//            return exercise;
//        }).orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));
        throw new IllegalStateException("TODO");

    }

    public void deleteExercise(Long id) {
//        exerciseRepository.findById(id)
//                .ifPresentOrElse(
//                        exerciseRepository::delete,
//                        () -> {
//                            throw new ExerciseNotFoundException("Exercise not found");
//                        }
//                );
        throw new IllegalStateException("TODO");

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
//        return exerciseRepository
//                .findAllByApprovedTrueAndTargetBodyPart(
//                        pageable,
//                        targetBodyPart
//                ).map(mapperTo::toExerciseFindViewModel);
        throw new IllegalStateException("TODO");
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByActiveTrue(Pageable pageable) {
//        return exerciseRepository
//                .findAllByApprovedTrue(
//                        pageable
//                ).map(mapperTo::toExerciseFindViewModel);
        throw new IllegalStateException("TODO");

    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByMovementTypeAndActiveTrue(Pageable pageable, MovementType movementType) {
//        return exerciseRepository
//                .findAllByApprovedTrueAndMovementType(
//                        pageable,
//                        movementType
//                ).map(mapperTo::toExerciseFindViewModel);
        throw new IllegalStateException("TODO");

    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByComplexityAndActiveTrue(Pageable pageable, Complexity complexity) {
//        return exerciseRepository
//                .findAllByApprovedTrueAndComplexity(
//                        pageable,
//                        complexity
//                ).map(mapperTo::toExerciseFindViewModel);
        throw new IllegalStateException("TODO");

    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByComplexityMovementTypeAndActiveTrue(Pageable pageable, Complexity complexity, MovementType movementType) {
//        return exerciseRepository
//                .findAllByApprovedTrueAndComplexityAndMovementType(
//                        pageable,
//                        complexity,
//                        movementType
//                ).map(mapperTo::toExerciseFindViewModel);
        throw new IllegalStateException("TODO");

    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByTargetBodyPartMovementTypeAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, MovementType movementType) {
//        return exerciseRepository
//                .findAllByApprovedTrueAndTargetBodyPartAndMovementType(
//                        pageable,
//                        targetBodyPart,
//                        movementType
//                ).map(mapperTo::toExerciseFindViewModel);
        throw new IllegalStateException("TODO");

    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByTargetBodyPartComplexityAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity) {
//        return exerciseRepository
//                .findAllByApprovedTrueAndTargetBodyPartAndComplexity(
//                        pageable,
//                        targetBodyPart,
//                        complexity
//                ).map(mapperTo::toExerciseFindViewModel);
        throw new IllegalStateException("TODO");

    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByTargetBodyPartComplexityMovementTypeAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity, MovementType movementType) {
//        return exerciseRepository
//                .findAllByApprovedTrueAndTargetBodyPartAndComplexityAndMovementType(
//                        pageable,
//                        targetBodyPart,
//                        complexity,
//                        movementType
//                ).map(mapperTo::toExerciseFindViewModel);
        throw new IllegalStateException("TODO");

    }


    private Page<ExerciseFindViewModel> getActiveExercisesWithNameContaining(String exerciseName, Pageable pageable) {
//        return exerciseRepository
//                .findAllByApprovedTrueAndNameContainingIgnoreCase(pageable, exerciseName)
//                .map(mapperTo::toExerciseFindViewModel);
        throw new IllegalStateException("TODO");

    }

    public ExerciseViewModel getExerciseView(Long id) {
        ResponseEntity<ExerciseViewModel> responseEntity = restClient.getExercise(id);

        return responseEntity.getBody();
    }


    public void editExercise(ExerciseEditBindingModel exerciseEdit) {
        restClient.editExercise(exerciseEdit);
    }

    public void deleteImageUrl(long id) {
//        imageUrlRepository.deleteById(id);
    }

    public ExerciseEditBindingModel getExerciseEditBindingModel(long id) {
        ResponseEntity<ExerciseEditBindingModel> responseEntity = restClient.getExerciseEditBindingModel(id);

        return responseEntity.getBody();
    }

    public List<ImageUrlViewModel> getExerciseImages(long exerciseId) {
//        List<ImageUrlViewModel> result = imageUrlRepository.findByExercise_Id(exerciseId).stream()
//                .map(imageUrl -> new ImageUrlViewModel(imageUrl.getId(), imageUrl.getUrl()))
//                .toList();
//
//        return result.isEmpty() ? new ArrayList<>() : result;
        throw new IllegalStateException("TODO");


    }

    public List<ExerciseNameAndIdViewModel> getExercisesForTargetBodyParts(List<TargetBodyPart> targetBodyParts) {
//        return exerciseRepository
//                .findAllByApprovedTrueAndTargetBodyPartIsIn(targetBodyParts)
//                .stream()
//                .map(mapperTo::toExerciseNameAndIdViewModel)
//                .toList();
        throw new IllegalStateException("TODO");

    }
//
    public ExerciseViewModel getExercise(Long id) {
//        return exerciseRepository
//                .findById(id)
//                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));
        throw new IllegalStateException("TODO");

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
//        return exerciseRepository
//                .findAllByApprovedTrue()
//                .stream()
//                .map(mapperTo::toExerciseFindViewModel)
//                .toList();
        throw new IllegalStateException("TODO");

    }
}