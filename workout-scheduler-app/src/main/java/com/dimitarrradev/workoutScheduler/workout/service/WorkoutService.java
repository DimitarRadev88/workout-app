package com.dimitarrradev.workoutScheduler.workout.service;

import com.dimitarrradev.workoutScheduler.errors.exception.WorkoutNotFoundException;
import com.dimitarrradev.workoutScheduler.exercise.dto.WorkoutExerciseServiceModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.service.DayScheduleService;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.user.service.UserService;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseWorkoutExerciseBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutViewServiceModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.dao.WorkoutRepository;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutEditServiceModel;
import com.dimitarrradev.workoutScheduler.workoutExercise.WorkoutExercise;
import com.dimitarrradev.workoutScheduler.workoutExercise.service.WorkoutExerciseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final UserService userService;
    private final WorkoutExerciseService workoutExerciseService;
    private final DayScheduleService dayScheduleService;

    @Transactional
    public long createWorkout(WorkoutAddBindingModel bindingModel, String username) {
        User user = userService.getUserEntityByUsername(username);

        DaySchedule schedule = dayScheduleService.getDayScheduleForDate(username, bindingModel.workoutDateTime().toLocalDate());

        Workout newWorkout = new Workout();
        newWorkout.setWorkoutType(bindingModel.workoutType());
        newWorkout.setTargetBodyParts(bindingModel.targetBodyParts());
        newWorkout.setWorkoutDateTime(bindingModel.workoutDateTime());
        newWorkout.setUser(user);

        newWorkout.setDaySchedule(schedule);

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
                ).orElseThrow(() -> new WorkoutNotFoundException("Workout not found"));
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
                .orElseThrow(() -> new WorkoutNotFoundException("The workout you were trying to add exercise to was not found"));

        WorkoutExercise trainingSet = workoutExerciseService.createWorkoutExercise(exerciseWorkoutExerciseBindingModel, workout);

        workout.getWorkoutExercises().add(trainingSet);

        workoutRepository.save(workout);
    }

    public void doEdit(long id, WorkoutEditBindingModel workoutEdit, String username) {
        Workout workout = workoutRepository
                .findWorkoutByIdAndUser_Username(id, username)
                .orElseThrow(() -> new WorkoutNotFoundException("The workout you were trying to edit was not found"));

        workout.setWorkoutType(workoutEdit.workoutType());
        workout.setTargetBodyParts(workoutEdit.targetBodyParts());

        workoutRepository.save(workout);
    }

    @Transactional
    public void doDelete(String username, Long id) {
        Optional<Workout> optionalWorkout = workoutRepository.findWorkoutByIdAndUser_Username(id, username);
        if (optionalWorkout.isPresent()) {
            Workout workout = optionalWorkout.get();
            DaySchedule daySchedule = workout.getDaySchedule();
            if (daySchedule.getWorkouts().size() <= 1) {
                dayScheduleService.deleteDailySchedule(username, daySchedule.getId());
            }
            workoutRepository.deleteById(id);
        } else {
            throw new WorkoutNotFoundException("The workout you were trying to delete was not found");
        }
    }
}
