package com.dimitarrradev.workoutScheduler.trainingSet.service;

import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import com.dimitarrradev.workoutScheduler.trainingSet.dao.TrainingSetRepository;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseTrainingSetsBindingModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class TrainingSetService {

    private final TrainingSetRepository trainingSetRepository;
    private final ExerciseService exerciseService;

    public TrainingSetService(TrainingSetRepository trainingSetRepository, ExerciseService exerciseService) {
        this.trainingSetRepository = trainingSetRepository;
        this.exerciseService = exerciseService;
    }

    public long getSetCountByWorkoutIdAndExerciseName(long id, String name) {
        return trainingSetRepository.countTrainingSetByWorkoutIdAndExerciseName(id, name);
    }


    public List<TrainingSet> createTrainingSets(ExerciseTrainingSetsBindingModel exerciseTrainingSetsBindingModel, Workout workout) {
        TrainingSet trainingSet = new TrainingSet();
        trainingSet.setExercise(exerciseService.getExercise(exerciseTrainingSetsBindingModel.exerciseId()));
        trainingSet.setMinReps(exerciseTrainingSetsBindingModel.trainingSet().minReps());
        trainingSet.setMaxReps(exerciseTrainingSetsBindingModel.trainingSet().maxReps());
        trainingSet.setRest(exerciseTrainingSetsBindingModel.trainingSet().rest());
        trainingSet.setWeight(exerciseTrainingSetsBindingModel.trainingSet().weight());
        trainingSet.setCompleted(Boolean.FALSE);
        trainingSet.setWorkout(workout);

        List<TrainingSet> trainingSetViewModels = new ArrayList<>();

        IntStream.range(0, exerciseTrainingSetsBindingModel.trainingSet().sets())
                .forEach(i -> {
                    trainingSetViewModels.add(trainingSetRepository.save(trainingSet));
                });

        return trainingSetViewModels;

    }
}
