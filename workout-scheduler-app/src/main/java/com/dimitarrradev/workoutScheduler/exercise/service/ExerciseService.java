package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.dao.ExerciseDao;
import com.dimitarrradev.workoutScheduler.exercise.dto.ExerciseForReviewViewModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public Page<ExerciseForReviewViewModel> getPaginatedAndSorted(int pageNo, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        return exerciseDao.findAll(pageable).map(exercise -> new ExerciseForReviewViewModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getEquipment(),
                exercise.getComplexity(),
                exercise.getAddedBy()
        ));
    }

}