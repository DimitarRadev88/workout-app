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
    public WorkoutExercise createWorkoutExercise(ExerciseWorkoutExerciseBindingModel exerciseTrainingSetsBindingModel, Workout workout) {
        WorkoutExercise trainingSet = new WorkoutExercise();
        trainingSet.setExercise(exerciseService.getExercise(exerciseTrainingSetsBindingModel.exerciseId()));
        trainingSet.setMinReps(exerciseTrainingSetsBindingModel.workoutExerciseBindingModel().minReps());
        trainingSet.setMaxReps(exerciseTrainingSetsBindingModel.workoutExerciseBindingModel().maxReps());
        trainingSet.setRest(exerciseTrainingSetsBindingModel.workoutExerciseBindingModel().rest());
        trainingSet.setWeight(exerciseTrainingSetsBindingModel.workoutExerciseBindingModel().weight());
        trainingSet.setSets(exerciseTrainingSetsBindingModel.workoutExerciseBindingModel().sets());
        trainingSet.setWorkout(workout);

        return workoutExerciseRepository.save(trainingSet);
    }

    public void delete(long id) {
        workoutExerciseRepository.deleteById(id);
    }

    public void doEdit(Long id, ExerciseInWorkoutEditBidingModel exerciseEdit) {
        WorkoutExercise trainingSet = workoutExerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercise " + id + " for current workout: not found"));

        trainingSet.setSets(exerciseEdit.count());
        trainingSet.setMinReps(exerciseEdit.minReps());
        trainingSet.setMaxReps(exerciseEdit.maxReps());
        if (exerciseEdit.minReps() > exerciseEdit.maxReps()) {
            trainingSet.setMaxReps(exerciseEdit.minReps());
        }
        trainingSet.setWeight(exerciseEdit.weight());
        trainingSet.setRest(exerciseEdit.rest());

        workoutExerciseRepository.save(trainingSet);
    }
}
