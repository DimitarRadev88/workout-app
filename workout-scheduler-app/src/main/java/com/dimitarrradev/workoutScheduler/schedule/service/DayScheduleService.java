package com.dimitarrradev.workoutScheduler.schedule.service;

import com.dimitarrradev.workoutScheduler.errors.exception.ScheduleDoesNotExistException;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.dao.DayScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.WeekScheduleViewModel;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.DayScheduleServiceViewModel;
import com.dimitarrradev.workoutScheduler.user.service.UserService;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutInScheduleViewModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DayScheduleService {

    private final DayScheduleRepository dayScheduleRepository;
    private final UserService userService;

    @Transactional
    public DayScheduleServiceViewModel getDailySchedule(String username) {
        DaySchedule daySchedule = dayScheduleRepository
                .findDayScheduleByUser_UsernameAndDate(username, LocalDate.now())
                .orElseThrow(() -> new ScheduleDoesNotExistException("Nothing scheduled for today"));

        return new DayScheduleServiceViewModel(
                daySchedule.getWorkouts().stream().map(workout -> new WorkoutInScheduleViewModel(
                        workout.getWorkoutDateTime(),
                        daySchedule.getIsCompleted()
                )).toList()
        );
    }

    @Transactional
    public DaySchedule getDayScheduleForDate(String username, LocalDate date) {
        return dayScheduleRepository
                .findDayScheduleByUser_UsernameAndDate(username, date)
                .orElse(dayScheduleRepository.save(createDaySchedule(username, date)));
    }

    private DaySchedule createDaySchedule(String username, LocalDate date) {
        DaySchedule schedule = new DaySchedule();
        schedule.setUser(userService.getUserEntityByUsername(username));
        schedule.setIsCompleted(false);
        schedule.setDate(date);

        return schedule;
    }

    public void deleteDailySchedule(String username, Long id) {
        if (!dayScheduleRepository.existsByIdAndUser_Username(id, username)) {
            throw new ScheduleDoesNotExistException("Schedule does not exist");
        }

        dayScheduleRepository.deleteByIdAndUser_Username(id, username);
    }

    public WeekScheduleViewModel getWeekSchedule(String name) {
        LocalDate monday = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        Map<DayOfWeek, List<DayScheduleServiceViewModel>> map = new LinkedHashMap<>();
        Arrays
                .stream(DayOfWeek.values())
                .forEach(day -> map.put(day, new ArrayList<>()));

        dayScheduleRepository
                .findAllByUser_UsernameAndDateIsBetweenOrderByDateAsc(name, monday, monday.plusDays(6))
                .forEach(schedule -> {
                    map.get(schedule.getDate().getDayOfWeek())
                            .add(new DayScheduleServiceViewModel(
                                    schedule.getWorkouts()
                                            .stream()
                                            .map(workout -> new WorkoutInScheduleViewModel(workout.getWorkoutDateTime(), false))
                                            .toList()));
                });

        return new WeekScheduleViewModel(map);
    }
}
