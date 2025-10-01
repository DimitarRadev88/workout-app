package com.dimitarrradev.workoutScheduler.workoutExercise.service;

import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.workoutExercise.WorkoutExercise;
import com.dimitarrradev.workoutScheduler.workoutExercise.dao.WorkoutExerciseRepository;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseInWorkoutEditBidingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseWorkoutExerciseBindingModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutExerciseService {

    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ExerciseService exerciseService;

    @Transactional
    public WorkoutExercise createWorkoutExercise(ExerciseWorkoutExerciseBindingModel exerciseWorkoutExerciseBindingModel, Workout workout) {
        WorkoutExercise workoutExercise = new WorkoutExercise(
                null,
                workout,
                exerciseService.getExercise(exerciseWorkoutExerciseBindingModel.exerciseId()),
                exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel().minReps(),
                exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel().maxReps(),
                0,
                exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel().weight(),
                exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel().rest(),
                exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel().sets()
        );

        return workoutExerciseRepository.save(workoutExercise);
    }

    public void delete(long id) {
        workoutExerciseRepository.deleteById(id);
    }

    public void doEdit(Long id, ExerciseInWorkoutEditBidingModel exerciseEdit) {
        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercise " + id + " for current workout: not found"));

        workoutExercise.setSets(exerciseEdit.count());
        workoutExercise.setMinReps(exerciseEdit.minReps());
        workoutExercise.setMaxReps(exerciseEdit.maxReps());
        if (exerciseEdit.minReps() > exerciseEdit.maxReps()) {
            workoutExercise.setMaxReps(exerciseEdit.minReps());
        }
        workoutExercise.setWeight(exerciseEdit.weight());
        workoutExercise.setRest(exerciseEdit.rest());

        workoutExerciseRepository.save(workoutExercise);
    }
}
