package com.dimitarrradev.workoutScheduler.workout.service;

import com.dimitarrradev.workoutScheduler.errors.exception.WorkoutNotFoundException;
import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.dto.WorkoutExerciseServiceModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.program.Program;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.service.ScheduleService;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.user.service.UserService;
import com.dimitarrradev.workoutScheduler.web.binding.*;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.dao.WorkoutRepository;
import com.dimitarrradev.workoutScheduler.workout.enums.Intensity;
import com.dimitarrradev.workoutScheduler.workout.enums.Volume;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutEditServiceModel;
import com.dimitarrradev.workoutScheduler.workoutExercise.WorkoutExercise;
import com.dimitarrradev.workoutScheduler.workoutExercise.service.WorkoutExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dimitarrradev.workoutScheduler.RandomValueGenerator.*;
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
    private WorkoutExerciseService workoutExerciseService;
    @Mock
    private ScheduleService scheduleService;
    @InjectMocks
    private WorkoutService workoutService;

    private Workout workout;
    private User user;
    private Exercise exercise;
    private WorkoutExercise workoutExercise;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("test-user");

        exercise = new Exercise();
        exercise.setName(randomExerciseName());

        workoutExercise = new WorkoutExercise(
                randomId(),
                null,
                exercise,
                5,
                10,
                null,
                22.5,
                60,
                4
        );

        DaySchedule daySchedule = new DaySchedule();
        daySchedule.setId(randomId());
        daySchedule.setUser(user);
        daySchedule.setDate(LocalDate.now());

        workout = new Workout(
                randomId(),
                LocalDateTime.now(),
                new ArrayList<>(List.of(workoutExercise)),
                new Program(),
                Intensity.HIGH,
                Volume.HIGH,
                randomWorkoutType(),
                user,
                daySchedule,
                randomTargetBodyPartsList()
        );
        daySchedule.setWorkouts(new ArrayList<>(List.of(workout)));
    }

    @Test
    void testCreateWorkoutCreatesWorkoutWithCorrectDataAndSavesItInRepositoryAndReturnsCorrectId() {
        WorkoutAddBindingModel bindingModel = new WorkoutAddBindingModel(
                randomWorkoutType(),
                randomTargetBodyPartsList(),
                LocalDateTime.now().plusDays(1L)
        );

        when(userService.getUserEntityByUsername(user.getUsername()))
                .thenReturn(user);

        Workout expected = new Workout();
        expected.setWorkoutType(bindingModel.workoutType());
        expected.setTargetBodyParts(bindingModel.targetBodyParts());
        expected.setWorkoutDateTime(bindingModel.workoutDateTime());
        expected.setUser(user);

        DaySchedule daySchedule = new DaySchedule();
        daySchedule.setId(randomId());
        daySchedule.setUser(user);
        daySchedule.setDate(bindingModel.workoutDateTime().toLocalDate());

        when(scheduleService.getDayScheduleForDate(user.getUsername(), bindingModel.workoutDateTime().toLocalDate()))
                .thenReturn(daySchedule);

        expected.setDaySchedule(daySchedule);

        Workout workoutWithId = new Workout();
        workoutWithId.setId(randomId());

        when(workoutRepository.save(expected))
                .thenReturn(workoutWithId);


        long id = workoutService.createWorkout(bindingModel, user.getUsername());

        assertThat(id)
                .isEqualTo(workoutWithId.getId());

        verify(
                workoutRepository,
                times(1)
        ).save(expected);
    }

    @Test
    void testGetWorkoutReturnsWorkoutEditServiceModelWithCorrectDataWhenWorkoutExistsInRepository() {
        when(workoutRepository.findWorkoutByIdAndUser_Username(workout.getId(), user.getUsername()))
                .thenReturn(Optional.of(workout));

        WorkoutEditServiceModel expected = new WorkoutEditServiceModel(
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
        );

        WorkoutEditServiceModel actual = workoutService.getWorkout(workout.getId(), user.getUsername());

        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    void testGetWorkoutThrowsWhenWorkoutDoesNotExistInRepository() {
        long randomId = randomId();
        when(workoutRepository.findWorkoutByIdAndUser_Username(randomId, "test-user"))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkoutNotFoundException.class,
                () -> workoutService.getWorkout(randomId, "test-user")
        );
    }

    @Test
    void testGetAllByUserUsernameReturnsListOfWorkoutViewServiceModelWithCorrectData() {
        when(workoutRepository.findAllByUser_UsernameOrderByWorkoutDateTimeDesc(user.getUsername()))
                .thenReturn(List.of(workout));

        List<WorkoutViewServiceModel> expected = List.of(
                new WorkoutViewServiceModel(
                        workout.getId(),
                        workout.getWorkoutType(),
                        workout.getWorkoutDateTime(),
                        workout.getTargetBodyParts().stream().map(TargetBodyPart::getName).collect(Collectors.joining(", ")
                        )
                )
        );

        List<WorkoutViewServiceModel> actual = workoutService.getAllByUserUsername(user.getUsername());

        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    void testAddWorkoutExerciseAddsAWorkoutExerciseInWorkoutExercisesListAndSavesItInRepositoryWhenWorkoutExistsInRepository() {
        Exercise exercise = new Exercise(
                randomId(),
                randomExerciseName(),
                workout.getTargetBodyParts().getFirst(),
                randomMovementType(),
                randomDescription(),
                Collections.emptyList(),
                Boolean.TRUE,
                user.getUsername(),
                randomComplexity()
        );

        ExerciseWorkoutExerciseBindingModel exerciseWorkoutExerciseBindingModel = new ExerciseWorkoutExerciseBindingModel(
                randomId(),
                new WorkoutExerciseBindingModel(3, 10, 12, 60, 50.0)
        );

        WorkoutExercise workoutExercise = new WorkoutExercise(
                randomId(),
                null,
                exercise,
                exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel().minReps(),
                exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel().maxReps(),
                0,
                exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel().weight(),
                exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel().rest(),
                exerciseWorkoutExerciseBindingModel.workoutExerciseBindingModel().sets());

        when(workoutRepository.findWorkoutByIdAndUser_Username(workout.getId(), user.getUsername()))
                .thenReturn(Optional.of(workout));

        when(workoutExerciseService.createWorkoutExercise(exerciseWorkoutExerciseBindingModel, workout))
                .thenReturn(workoutExercise);


        workoutService.addWorkoutExercise(workout.getId(), exerciseWorkoutExerciseBindingModel, user.getUsername());

        verify(
                workoutRepository,
                times(1)
        ).save(workout);
    }

    @Test
    void testAddWorkoutExerciseThrowsWhenWorkoutDoesNotExistInRepository() {
        when(workoutRepository.findWorkoutByIdAndUser_Username(workout.getId(), user.getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(WorkoutNotFoundException.class,
                () -> workoutService
                        .addWorkoutExercise(
                                workout.getId(),
                                new ExerciseWorkoutExerciseBindingModel(randomId(), null),
                                user.getUsername()
                        )
        );
    }

    @Test
    void testDoEditSavesWorkoutWithNewDataWhenWorkoutExistsInRepository() {
        when(workoutRepository.findWorkoutByIdAndUser_Username(workout.getId(), user.getUsername()))
                .thenReturn(Optional.of(workout));

        WorkoutEditBindingModel workoutEditBindingModel = new WorkoutEditBindingModel(
                WorkoutType.CARDIO,
                randomTargetBodyPartsList()
        );

        workoutService.doEdit(workout.getId(), workoutEditBindingModel, user.getUsername());
        verify(
                workoutRepository,
                times(1)
        ).save(workout);
    }

    @Test
    void testDoEditThrowsWhenWorkoutDoesNotExistInRepository() {
        when(workoutRepository.findWorkoutByIdAndUser_Username(workout.getId(), user.getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(WorkoutNotFoundException.class,
                () -> workoutService.doEdit(
                        workout.getId(),
                        new WorkoutEditBindingModel(null, null),
                        user.getUsername())
        );
    }

    @Test
    void testDoDeleteDeletesWorkoutAndScheduleIfOnlyOneWorkoutExistsInRepositoryForTheDay() {
        when(workoutRepository.findWorkoutByIdAndUser_Username(workout.getId(), user.getUsername()))
                .thenReturn(Optional.of(workout));

        workoutService.doDelete(user.getUsername(), workout.getId());

        verify(
                workoutRepository,
                times(1)
        ).deleteById(workout.getId());

        verify(
                scheduleService,
                times(1)
        ).doDelete(user.getUsername(), workout.getDaySchedule().getId());
    }

    @Test
    void testDoDeleteDeletesWorkoutButNotScheduleIfMoreThanOneWorkoutExistsInRepositoryForTheDay() {
        when(workoutRepository.findWorkoutByIdAndUser_Username(workout.getId(), user.getUsername()))
                .thenReturn(Optional.of(workout));

        workout.getDaySchedule().getWorkouts().add(new Workout());

        workoutService.doDelete(user.getUsername(), workout.getId());

        verify(
                workoutRepository,
                times(1)
        ).deleteById(workout.getId());

        verify(
                scheduleService,
                never()
        ).doDelete(user.getUsername(), workout.getDaySchedule().getId());
    }

    @Test
    void testDoDeleteThrowsWhenWorkoutDoesNotExistInRepository() {
        when(workoutRepository.findWorkoutByIdAndUser_Username(workout.getId(), user.getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkoutNotFoundException.class,
                () -> workoutService.doDelete(user.getUsername(), workout.getId())
        );
    }

}