package com.dimitarrradev.workoutScheduler.schedule.service;

import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.dao.DayScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.dao.WeekScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.DailyScheduleServiceViewModel;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutServiceViewModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final DayScheduleRepository dayScheduleRepository;
    private final WeekScheduleRepository weekScheduleRepository;

    public ScheduleService(DayScheduleRepository dayScheduleRepository, WeekScheduleRepository weekScheduleRepository) {
        this.dayScheduleRepository = dayScheduleRepository;
        this.weekScheduleRepository = weekScheduleRepository;
    }

    public DailyScheduleServiceViewModel getDailySchedule(String username) {
        DaySchedule daySchedule = dayScheduleRepository
                .findDayScheduleByUser_UsernameAndDate(username, LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("No schedule found"));

        return new DailyScheduleServiceViewModel(
                daySchedule.getWorkout().stream().map(workout -> new WorkoutServiceViewModel(
                        workout.getExerciseTrainingSets()
                                .keySet()
                                .stream()
                                .map(exercise -> exercise.getComplexity()
                                        .getName()).distinct()
                                .collect(Collectors.joining(", ")
                                ),
                        workout.getIntensity().getName(),
                        workout.getVolume().getValue(),
                        workout.getWorkoutDateTime(),
                        daySchedule.getCompleted()
                )).toList()
        );
    }

}
