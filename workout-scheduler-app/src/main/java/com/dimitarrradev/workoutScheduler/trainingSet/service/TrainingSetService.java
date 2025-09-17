package com.dimitarrradev.workoutScheduler.trainingSet.service;

import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import com.dimitarrradev.workoutScheduler.trainingSet.dao.TrainingSetRepository;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseTrainingSetsBindingModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import org.springframework.stereotype.Service;

@Service
public class TrainingSetService {

    private final TrainingSetRepository trainingSetRepository;
    private final ExerciseService exerciseService;

    public TrainingSetService(TrainingSetRepository trainingSetRepository, ExerciseService exerciseService) {
        this.trainingSetRepository = trainingSetRepository;
        this.exerciseService = exerciseService;
    }

    public long getSetCountByWorkoutIdAndExerciseName(long id, String name, Integer minReps, Integer maxReps, Double weight) {
        return trainingSetRepository.countTrainingSetByWorkoutIdAndExerciseNameAndMinRepsAndMaxRepsAndWeight(id, name, minReps, maxReps, weight);
    }


    public TrainingSet createTrainingSet(ExerciseTrainingSetsBindingModel exerciseTrainingSetsBindingModel, Workout workout) {
        TrainingSet trainingSet = new TrainingSet();
        trainingSet.setExercise(exerciseService.getExercise(exerciseTrainingSetsBindingModel.exerciseId()));
        trainingSet.setMinReps(exerciseTrainingSetsBindingModel.trainingSet().minReps());
        trainingSet.setMaxReps(exerciseTrainingSetsBindingModel.trainingSet().maxReps());
        trainingSet.setRest(exerciseTrainingSetsBindingModel.trainingSet().rest());
        trainingSet.setWeight(exerciseTrainingSetsBindingModel.trainingSet().weight());
        trainingSet.setCount(exerciseTrainingSetsBindingModel.trainingSet().count());
        trainingSet.setWorkout(workout);

        return trainingSetRepository.save(trainingSet);
    }

    public void delete(long id) {
        trainingSetRepository.deleteById(id);
    }
}
