package com.dimitarrradev.workoutScheduler.workout.service;

import com.dimitarrradev.workoutScheduler.exercise.dto.TrainingSetsServiceModel;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.trainingSet.service.TrainingSetService;
import com.dimitarrradev.workoutScheduler.user.service.UserService;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutAddBindingModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.dao.WorkoutRepository;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutEditServiceModel;
import org.springframework.stereotype.Service;

@Service
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final UserService userService;
    private final TrainingSetService trainingSetService;

    public WorkoutService(WorkoutRepository workoutRepository, UserService userService, TrainingSetService trainingSetService) {
        this.workoutRepository = workoutRepository;
        this.userService = userService;
        this.trainingSetService = trainingSetService;
    }

    public long createWorkout(WorkoutAddBindingModel workout, String username) {
        userService.getUserEntityByUsername(username);

        Workout newWorkout = new Workout();
        newWorkout.setWorkoutType(workout.workoutType());
        newWorkout.setTargetBodyParts(workout.targetBodyParts());
        newWorkout.setWorkoutDateTime(workout.workoutDateTime());
        newWorkout.setUser(userService.getUserEntityByUsername(username));
        return workoutRepository.save(newWorkout).getId();
    }

    public WorkoutEditServiceModel getWorkout(long id) {
        return workoutRepository.findWorkoutById(id).map(workout -> new WorkoutEditServiceModel(
                        workout.getWorkoutType(),
                        workout.getTargetBodyParts(),
                        workout.getWorkoutDateTime(),
                        workout.getTrainingSets().stream().map(trainingSet -> new TrainingSetsServiceModel(
                                trainingSet.getId(),
                                trainingSet.getExercise().getName(),
                                trainingSetService.getSetCountByWorkoutIdAndExerciseName(id, trainingSet.getExercise().getName()),
                                trainingSet.getMinReps(),
                                trainingSet.getMaxReps(),
                                trainingSet.getWeight(),
                                trainingSet.getRest()
                        )).toList()
                )
        ).orElseThrow(() -> new IllegalArgumentException("Workout not found"));
    }
}
