package com.dimitarrradev.workoutScheduler.workoutExercise.service;

import com.dimitarrradev.workoutScheduler.errors.exception.WorkoutExerciseNotFoundException;
import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseWorkoutExerciseBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutExerciseBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.enums.Intensity;
import com.dimitarrradev.workoutScheduler.workout.enums.Volume;
import com.dimitarrradev.workoutScheduler.workoutExercise.WorkoutExercise;
import com.dimitarrradev.workoutScheduler.workoutExercise.dao.WorkoutExerciseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static com.dimitarrradev.workoutScheduler.RandomValueGenerator.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutExerciseServiceUnitTests {

    @Mock
    private WorkoutExerciseRepository workoutExerciseRepository;
    @Mock
    private ExerciseService exerciseService;
    @InjectMocks
    private WorkoutExerciseService workoutExerciseService;

    @Test
    void testCreateWorkoutExerciseCreatesNewWorkoutExerciseWithCorrectData() {
        WorkoutExerciseBindingModel workoutExerciseBindingModel = new WorkoutExerciseBindingModel(4, 10, 12, 60, 80.00);
        ExerciseWorkoutExerciseBindingModel exerciseWorkoutExerciseBindingModel = new ExerciseWorkoutExerciseBindingModel(randomId(), workoutExerciseBindingModel);

        Workout workout = new Workout(
                randomId(),
                LocalDateTime.now(),
                new ArrayList<>(),
                null,
                Intensity.HIGH,
                Volume.HIGH,
                randomWorkoutType(),
                null,
                null,
                randomTargetBodyPartsList()
        );

        Exercise exercise = new Exercise(
                exerciseWorkoutExerciseBindingModel.exerciseId(),
                randomExerciseName(),
                randomTargetBodyPart(),
                randomMovementType(),
                randomDescription(),
                Collections.emptyList(),
                Boolean.TRUE,
                "user",
                randomComplexity());

        WorkoutExercise expected = new WorkoutExercise(
                null,
                workout,
                exercise,
                workoutExerciseBindingModel.minReps(),
                workoutExerciseBindingModel.maxReps(),
                0,
                workoutExerciseBindingModel.weight(),
                workoutExerciseBindingModel.rest(),
                workoutExerciseBindingModel.sets());

        when(exerciseService.getExercise(exercise.getId()))
                .thenReturn(exercise);

        workoutExerciseService.createWorkoutExercise(exerciseWorkoutExerciseBindingModel, workout);

        verify(
                workoutExerciseRepository,
                times(1)
        ).save(expected);
    }

    @Test
    void testDeleteDeletesExerciseFromRepository() {
        long id = randomId();
        String username = "user";

        when(workoutExerciseRepository.existsByIdAndWorkout_User_Username(id,  username))
                .thenReturn(true);

        workoutExerciseService.delete(id, username);

        verify(
                workoutExerciseRepository,
                times(1)
        ).deleteById(id);
    }

    @Test
    void testDeleteThrowsWhenWorkoutExerciseDoesNotExistForUser() {
        long id = randomId();
        String username = "user";

        when(workoutExerciseRepository.existsByIdAndWorkout_User_Username(id,  username))
                .thenReturn(false);


        assertThrows(
                WorkoutExerciseNotFoundException.class,
                () -> workoutExerciseService.delete(id, username)
        );

        verify(
                workoutExerciseRepository,
                never()
        ).deleteById(id);
    }

    @Test
    void testDoEditSavesWorkoutExerciseWithNewDataInRepository() {
        long id = randomId();
        long workoutId = randomId();
        String username = "user";
        Exercise exercise = new Exercise(
                randomId(),
                randomExerciseName(),
                randomTargetBodyPart(),
                randomMovementType(),
                randomDescription(),
                Collections.emptyList(),
                Boolean.TRUE,
                username,
                randomComplexity()
        );

        WorkoutExercise workoutExercise = new WorkoutExercise(
                id,
                null,
                exercise,
                4,
                6,
                0,
                80.0,
                90,
                3
        );

        WorkoutExerciseEditBindingModel bindingModel = new WorkoutExerciseEditBindingModel(
                4,
                10,
                12,
                60.0,
                60
        );

        WorkoutExercise expected = new WorkoutExercise(
                workoutExercise.getId(),
                null,
                exercise,
                bindingModel.minReps(),
                bindingModel.maxReps(),
                0,
                bindingModel.weight(),
                bindingModel.rest(),
                bindingModel.sets()
        );

        when(workoutExerciseRepository.findByIdAndWorkout_IdAndWorkout_User_Username(id, workoutId, username))
                .thenReturn(Optional.of(workoutExercise));

        workoutExerciseService.doEdit(id, workoutId, username, bindingModel);

        verify(
                workoutExerciseRepository,
                times(1))
                .save(expected);
    }

    @Test
    void testDoEditWithMaxRepsLessThanMinSetsChangesMaxRepsValueToMinRepsValueSavesWorkoutExerciseWithNewDataInRepository() {
        long id = randomId();
        long workoutId = randomId();
        String username = "user";
        Exercise exercise = new Exercise(
                randomId(),
                randomExerciseName(),
                randomTargetBodyPart(),
                randomMovementType(),
                randomDescription(),
                Collections.emptyList(),
                Boolean.TRUE,
                username,
                randomComplexity()
        );

        WorkoutExercise workoutExercise = new WorkoutExercise(
                id,
                null,
                exercise,
                4,
                6,
                0,
                80.0,
                90,
                3
        );

        WorkoutExerciseEditBindingModel bindingModel = new WorkoutExerciseEditBindingModel(
                4,
                10,
                8,
                60.0,
                60
        );

        WorkoutExercise expected = new WorkoutExercise(
                workoutExercise.getId(),
                null,
                exercise,
                bindingModel.minReps(),
                bindingModel.minReps(),
                0,
                bindingModel.weight(),
                bindingModel.rest(),
                bindingModel.sets()
        );

        when(workoutExerciseRepository.findByIdAndWorkout_IdAndWorkout_User_Username(id, workoutId, username))
                .thenReturn(Optional.of(workoutExercise));

        workoutExerciseService.doEdit(id, workoutId, username, bindingModel);

        verify(
                workoutExerciseRepository,
                times(1))
                .save(expected);
    }

    @Test
    void testDoEditThrowsWhenExerciseNotFoundInRepository() {
        long id = randomId();
        long workoutId = randomId();

        String username = "user";

        when(workoutExerciseRepository.findByIdAndWorkout_IdAndWorkout_User_Username(id, workoutId, username))
                .thenReturn(Optional.empty());

        assertThrows(WorkoutExerciseNotFoundException.class,
                () -> workoutExerciseService.doEdit(id, workoutId, username, null));
    }

}