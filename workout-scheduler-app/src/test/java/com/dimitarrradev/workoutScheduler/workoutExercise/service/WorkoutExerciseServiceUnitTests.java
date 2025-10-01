package com.dimitarrradev.workoutScheduler.workoutExercise.service;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseWorkoutExerciseBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.WorkoutExerciseBindingModel;
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

import static com.dimitarrradev.workoutScheduler.RandomValueGenerator.*;
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
        workoutExerciseService.delete(id);

        verify(
                workoutExerciseRepository,
                times(1)
        ).deleteById(id);
    }

}