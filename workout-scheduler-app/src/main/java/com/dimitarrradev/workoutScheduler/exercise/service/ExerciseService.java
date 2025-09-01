package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.dao.ExerciseDao;
import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseFindViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseForReviewViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.PageAndExerciseFindServiceView;
import com.dimitarrradev.workoutScheduler.exercise.dto.PageAndExerciseReviewServiceView;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseFindBindingModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    private final ExerciseDao exerciseDao;

    public ExerciseService(ExerciseDao exerciseDao) {
        this.exerciseDao = exerciseDao;
    }

    public void addExerciseForReview(ExerciseAddBindingModel exerciseAdd) {
        if (exerciseDao.existsExerciseByName(exerciseAdd.exerciseName())) {
            throw new IllegalArgumentException("Exercise already exists");
        }

        Exercise exercise = new Exercise();
        exercise.setName(exerciseAdd.exerciseName());
        exercise.setDescription(exerciseAdd.description());
        exercise.setTargetBodyPart(exerciseAdd.bodyPart());
        exercise.setApproved(false);
        exercise.setAddedBy(exerciseAdd.addedBy());
        exercise.setComplexity(exerciseAdd.complexity());

        exerciseDao.save(exercise);
    }

    public long getExercisesForReviewCount() {
        return exerciseDao.countAllByApprovedFalse();
    }

    public PageAndExerciseReviewServiceView getPaginatedAndSortedDataAndExerciseActiveFalse(int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<ExerciseForReviewViewModel> page = exerciseDao
                .findAllByApprovedIs(pageable, false)
                .map(exercise -> new ExerciseForReviewViewModel(
                        exercise.getId(),
                        exercise.getName(),
                        exercise.getDescription(),
                        exercise.getEquipment(),
                        exercise.getComplexity(),
                        exercise.getAddedBy()
                ));

        return new PageAndExerciseReviewServiceView(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                String.format("Showing %d to %d of %d exercises",
                        page.getTotalElements() == 0 ? 0 : (pageNumber - 1) * pageSize + 1,
                        pageNumber < page.getTotalPages() ? (long) pageNumber * pageSize : page.getTotalElements(),
                        page.getTotalElements()
                ),
                List.of(5, 10, 25, 50)
        );
    }

    public void approveExercise(Long id) {
        Optional<Exercise> exerciseOptional = exerciseDao.findById(id);

        if (exerciseOptional.isEmpty()) {
            throw new IllegalArgumentException("Exercise not found");
        }

        exerciseOptional.ifPresent(exercise -> {
            exercise.setApproved(true);
            exerciseDao.save(exercise);
        });
    }

    public void deleteExercise(Long id) {
        Optional<Exercise> exerciseOptional = exerciseDao.findById(id);

        if (exerciseOptional.isEmpty()) {
            throw new IllegalArgumentException("Exercise not found");
        }

        exerciseOptional.ifPresent(exerciseDao::delete);
    }

    public PageAndExerciseFindServiceView getPaginatedAndSortedDataAndExerciseActiveTrue(ExerciseFindBindingModel exerciseFind, int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<ExerciseFindViewModel> page = null;

        if (exerciseFind.targetBodyPart() != null) {
            if (exerciseFind.complexity() != null && !exerciseFind.complexity().equals(Complexity.ALL)) {
                page = exerciseDao
                        .findAllByApprovedTrueAndTargetBodyPartAndComplexity(pageable, exerciseFind.targetBodyPart(), exerciseFind.complexity())
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity()
                        ));
            } else {
                page = exerciseDao
                        .findAllByApprovedTrueAndTargetBodyPart(pageable, exerciseFind.targetBodyPart())
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity()
                        ));
            }
        } else {
            if (exerciseFind.complexity() != null && !exerciseFind.complexity().equals(Complexity.ALL)) {
                page = exerciseDao
                        .findAllByApprovedTrueAndComplexity(pageable, exerciseFind.complexity())
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity()
                        ));
            } else {
                page = exerciseDao
                        .findAllByApprovedIs(pageable, true)
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity()
                        ));
            }

        }


        return new PageAndExerciseFindServiceView(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                String.format("Showing %d to %d of %d exercises",
                        page.getTotalElements() == 0 ? 0 : (pageNumber - 1) * pageSize + 1,
                        pageNumber < page.getTotalPages() ? (long) pageNumber * pageSize : page.getTotalElements(),
                        page.getTotalElements()
                ),
                List.of(5, 10, 25, 50)
        );
    }

}