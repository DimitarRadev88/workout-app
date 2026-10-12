package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.config.rest.ExerciseRestClient;
import com.dimitarrradev.workoutScheduler.errors.exception.ExerciseNotFoundException;
import com.dimitarrradev.workoutScheduler.exercise.dto.PageInformation;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseFindBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.view.*;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExerciseService {

    private final ExerciseRestClient restClient;

    public ExerciseService(ExerciseRestClient restClient) {
        this.restClient = restClient;
    }

    public String addExerciseForReview(ExerciseAddBindingModel exerciseAdd) {
        ExerciseRestClient.ExerciseResponse added = restClient.addExercise(exerciseAdd);

        log.info("Exercise added for review: {}", added.id());

        return "Exercise " + added.name() + " was sent for review";
    }

    public long getExercisesForReviewCount() {
        return restClient.getExercisesForReviewCount().getBody();
    }


    public Page<ExerciseForReviewViewModel> getExercisesForReviewPage(int pageNumber, int pageSize, String sortDirection) {
        return restClient.getExercisesForReviewPage(pageNumber, pageSize, sortDirection);
    }

    public void approveExercise(Long id) {
        restClient.approveExercise(id);
    }

    public void deleteExercise(Long id) {
        restClient.deleteExercise(id);
    }

    public Page<ExerciseFindViewModel> findActiveExercisesPage(ExerciseFindBindingModel exerciseFind, int pageNumber, int pageSize, String sortDirection) {
        return restClient.searchExercises(
                exerciseFind.name(),
                exerciseFind.targetBodyPart(),
                exerciseFind.complexity(),
                exerciseFind.movementType(),
                pageNumber - 1,
                pageSize,
                sortDirection
        );
    }

    public ExerciseViewModel getExerciseView(Long id) {
        ResponseEntity<ExerciseViewModel> responseEntity = restClient.getExercise(id);

        return responseEntity.getBody();
    }


    public void editExercise(ExerciseEditBindingModel exerciseEdit) {
        restClient.editExercise(exerciseEdit);
    }

    public void deleteImageUrl(long exerciseId, long id) {
        restClient.deleteExerciseImage(exerciseId, id);
    }

    public ExerciseEditBindingModel getExerciseEditBindingModel(long id) {
        ResponseEntity<ExerciseEditBindingModel> responseEntity = restClient.getExerciseEditBindingModel(id);

        return responseEntity.getBody();
    }

    public List<ImageUrlViewModel> getExerciseImages(long exerciseId) {
        return restClient.getExerciseImages(exerciseId);
    }

    public List<ExerciseNameAndIdViewModel> getExercisesForTargetBodyParts(List<TargetBodyPart> targetBodyParts) {
        return restClient.getExercises(targetBodyParts)
                .stream()
                .map(exercise -> new ExerciseNameAndIdViewModel(exercise.id(), exercise.name()))
                .toList();
    }

    public Map<Long, String> getExerciseNames(Collection<Long> exerciseIds) {
        return exerciseIds.stream()
                .distinct()
                .collect(Collectors.toMap(id -> id, this::getExerciseName));
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
        return restClient.getExercises(List.of())
                .stream()
                .map(exercise -> new ExerciseFindViewModel(exercise.id(), exercise.name(), exercise.complexity(), exercise.movementType()))
                .toList();
    }

    private String getExerciseName(Long id) {
        try {
            return getExerciseView(id).name();
        } catch (ExerciseNotFoundException e) {
            log.warn("Exercise {} not found in exercise-service", id);
            return "Unknown exercise";
        }
    }
}