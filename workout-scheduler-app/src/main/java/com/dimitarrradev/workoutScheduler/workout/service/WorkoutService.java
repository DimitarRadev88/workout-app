package com.dimitarrradev.workoutScheduler.workout.service;

import com.dimitarrradev.workoutScheduler.exercise.dto.WorkoutExerciseServiceModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.dao.DayScheduleRepository;
import com.dimitarrradev.workoutScheduler.workoutExercise.WorkoutExercise;
import com.dimitarrradev.workoutScheduler.workoutExercise.service.WorkoutExerciseService;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.user.service.UserService;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseWorkoutExerciseBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutViewServiceModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.dao.WorkoutRepository;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutEditServiceModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final UserService userService;
    private final WorkoutExerciseService workoutExerciseService;
    private final DayScheduleRepository dayScheduleRepository;

    @Transactional
    public long createWorkout(WorkoutAddBindingModel workout, String username) {
        User user = userService.getUserEntityByUsername(username);

        Workout newWorkout = new Workout();
        newWorkout.setWorkoutType(workout.workoutType());
        newWorkout.setTargetBodyParts(workout.targetBodyParts());
        newWorkout.setWorkoutDateTime(workout.workoutDateTime());
        newWorkout.setUser(user);
        DaySchedule daySchedule = new DaySchedule();
        daySchedule.setUser(user);
        daySchedule.setDate(workout.workoutDateTime().toLocalDate());
        DaySchedule save = dayScheduleRepository.save(daySchedule);

        newWorkout.setDaySchedule(save);

        return workoutRepository.save(newWorkout).getId();
    }

    public WorkoutEditServiceModel getWorkout(long id, String username) {
        return workoutRepository.findWorkoutByIdAndUser_Username(id, username)
                .map(workout -> new WorkoutEditServiceModel(
                                workout.getWorkoutType(),
                                workout.getTargetBodyParts(),
                                workout.getWorkoutDateTime(),
                                workout.getWorkoutExercises().stream().map(workoutExercise -> new WorkoutExerciseServiceModel(
                                        workoutExercise.getId(),
                                        workoutExercise.getExercise().getName(),
                                        workoutExercise.getSets(),
                                        workoutExercise.getMinReps(),
                                        workoutExercise.getMaxReps(),
                                        workoutExercise.getWeight(),
                                        workoutExercise.getRest()
                                )).toList()
                        )
                ).orElseThrow(() -> new IllegalArgumentException("Workout not found"));
    }

    public List<WorkoutViewServiceModel> getAllByUserUsername(String username) {
        return workoutRepository.findAllByUser_UsernameOrderByWorkoutDateTimeDesc(username)
                .stream()
                .map(workout -> new WorkoutViewServiceModel(
                                workout.getId(),
                                workout.getWorkoutType(),
                                workout.getWorkoutDateTime(),
                                workout.getTargetBodyParts()
                                        .stream()
                                        .map(TargetBodyPart::getName)
                                        .collect(Collectors.joining(", "))
                        )
                ).toList();
    }

    @Transactional
    public void addWorkoutExercise(long id, ExerciseWorkoutExerciseBindingModel exerciseWorkoutExerciseBindingModel, String username) {
        Workout workout = workoutRepository
                .findWorkoutByIdAndUser_Username(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));

        WorkoutExercise trainingSet = workoutExerciseService.createWorkoutExercise(exerciseWorkoutExerciseBindingModel, workout);

        workout.getWorkoutExercises().add(trainingSet);

        workoutRepository.save(workout);
    }

    public void doEdit(long id, WorkoutEditBindingModel workoutEdit, String username) {
        Workout workout = workoutRepository
                .findWorkoutByIdAndUser_Username(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));

        workout.setWorkoutType(workoutEdit.workoutType());
        workout.setTargetBodyParts(workoutEdit.targetBodyParts());

        workoutRepository.save(workout);
    }
}
