package com.dimitarrradev.workoutScheduler.workout.service;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.dto.TrainingSetsServiceModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.program.Program;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import com.dimitarrradev.workoutScheduler.trainingSet.service.TrainingSetService;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.user.service.UserService;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutAddBindingModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.dao.WorkoutRepository;
import com.dimitarrradev.workoutScheduler.workout.enums.Intensity;
import com.dimitarrradev.workoutScheduler.workout.enums.Volume;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutEditServiceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceUnitTests {

    @Mock
    private WorkoutRepository workoutRepository;
    @Mock
    private UserService userService;
    @Mock
    private TrainingSetService trainingSetService;
    @InjectMocks
    private WorkoutService workoutService;

    @Test
    void testCreateWorkoutCreatesWorkoutWithCorrectDataAndSavesItInRepositoryAndReturnsCorrectId() {
        User user = new User();
        user.setUsername("test-user");

        WorkoutAddBindingModel bindingModel = new WorkoutAddBindingModel(
                getRandomWorkoutType(),
                getRandomTargetBodyPartsList(),
                LocalDateTime.now().plusDays(1L)
        );

        when(userService.getUserEntityByUsername(user.getUsername())).thenReturn(user);

        Workout expected = new Workout();
        expected.setWorkoutType(bindingModel.workoutType());
        expected.setTargetBodyParts(bindingModel.targetBodyParts());
        expected.setWorkoutDateTime(bindingModel.workoutDateTime());
        expected.setUser(user);

        Workout workoutWithId = new Workout();
        workoutWithId.setId(1L);
        when(workoutRepository.save(expected)).thenReturn(workoutWithId);

        long id = workoutService.createWorkout(bindingModel, user.getUsername());

        assertThat(id).isEqualTo(workoutWithId.getId());

        verify(workoutRepository, times(1))
                .save(expected);
    }

    @Test
    void testGetWorkoutReturnsWorkoutEditServiceModelWithCorrectDataWhenWorkoutExistsInRepository() {
        User user = new User();
        user.setUsername("test-user");
        when(userService.getUserEntityByUsername(user.getUsername())).thenReturn(user);

        Exercise exercise = new Exercise();
        exercise.setName("test exercise");
        TrainingSet trainingSet = new TrainingSet(
                1L,
                null,
                exercise,
                5,
                10,
                null,
                22.5,
                60,
                4
        );

        Workout workout = new Workout(
                ThreadLocalRandom.current().nextLong(),
                LocalDateTime.now(),
                List.of(trainingSet),
                new Program(),
                Intensity.HIGH,
                Volume.HIGH,
                WorkoutType.BODYBUILDING,
                user,
                new DaySchedule(),
                getRandomTargetBodyPartsList()
        );

        when(workoutRepository.findWorkoutByIdAndUser_Username(workout.getId(), user.getUsername()))
                .thenReturn(Optional.of(workout));

        WorkoutEditServiceModel expected = new WorkoutEditServiceModel(
                workout.getWorkoutType(),
                workout.getTargetBodyParts(),
                workout.getWorkoutDateTime(),
                workout.getTrainingSets().stream().map(set -> new TrainingSetsServiceModel(
                        set.getId(),
                        set.getExercise().getName(),
                        set.getCount(),
                        set.getMinReps(),
                        set.getMaxReps(),
                        set.getWeight(),
                        set.getRest()
                )).toList()
        );

        assertThat(workoutService.getWorkout(workout.getId(), user.getUsername()))
                .isEqualTo(expected);
    }

    @Test
    void testGetWorkoutThrowsWhenWorkoutDoesNotExistInRepository() {
        when(userService.getUserEntityByUsername("test-user"))
                .thenReturn(new User());
        when(workoutRepository.findWorkoutByIdAndUser_Username(1L, "test-user"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> workoutService.getWorkout(1L, "test-user"));
    }

    private WorkoutType getRandomWorkoutType() {
        return WorkoutType.values()[ThreadLocalRandom.current().nextInt(WorkoutType.values().length - 1)];
    }

    private List<TargetBodyPart> getRandomTargetBodyPartsList() {
        int count = ThreadLocalRandom.current().nextInt(TargetBodyPart.values().length - 1);

        List<TargetBodyPart> targetBodyPartNames = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(TargetBodyPart.values().length - 1);
            TargetBodyPart targetBodyPart = TargetBodyPart.values()[randomIndex];
            if (targetBodyPartNames.contains(targetBodyPart)) {
                i--;
            } else {
                targetBodyPartNames.add(targetBodyPart);
            }
        }

        return targetBodyPartNames;
    }

}