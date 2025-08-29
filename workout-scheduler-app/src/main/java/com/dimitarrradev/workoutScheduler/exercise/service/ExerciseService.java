package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.dao.ExerciseDao;
import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseForReviewViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.PageAndExerciseServiceView;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
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
        exercise.setActive(false);
        exercise.setAddedBy(exerciseAdd.addedBy());
        exercise.setComplexity(exerciseAdd.complexity());

        exerciseDao.save(exercise);
    }

    public long getExercisesForReviewCount() {
        return exerciseDao.countAllByActiveIsFalse();
    }

    public PageAndExerciseServiceView getPaginatedAndSortedDataAndExercise(int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<ExerciseForReviewViewModel> page = exerciseDao
                .findAllByActiveFalse(pageable)
                .map(exercise -> new ExerciseForReviewViewModel(
                        exercise.getId(),
                        exercise.getName(),
                        exercise.getDescription(),
                        exercise.getEquipment(),
                        exercise.getComplexity(),
                        exercise.getAddedBy()
                ));

        return new PageAndExerciseServiceView(
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
            exercise.setActive(true);
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
}