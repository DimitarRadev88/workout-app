package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.dao.ExerciseDao;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import jakarta.validation.Valid;
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

        exerciseDao.save(exercise);
    }
}
