package com.dimitarrradev.workoutScheduler.schedule.service;

import com.dimitarrradev.workoutScheduler.errors.exception.ScheduleDoesNotExistException;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.WeekSchedule;
import com.dimitarrradev.workoutScheduler.schedule.dao.DayScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.dao.WeekScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.dto.WeeklyScheduleViewModel;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.DailyScheduleServiceViewModel;
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
public class ScheduleService {

    private final DayScheduleRepository dayScheduleRepository;
    private final WeekScheduleRepository weekScheduleRepository;
    private final UserService userService;

    @Transactional
    public DailyScheduleServiceViewModel getDailySchedule(String username) {
        DaySchedule daySchedule = dayScheduleRepository
                .findDayScheduleByUser_UsernameAndDate(username, LocalDate.now())
                .orElseThrow(() -> new ScheduleDoesNotExistException("Nothing scheduled for today"));

        return new DailyScheduleServiceViewModel(
                daySchedule.getWorkouts().stream().map(workout -> new WorkoutInScheduleViewModel(
                        workout.getWorkoutDateTime(),
                        daySchedule.getIsCompleted()
                )).toList()
        );
    }

    @Transactional
    public DaySchedule getDayScheduleForDate(String username, LocalDate date) {
        var dayScheduleOptional = dayScheduleRepository
                .findDayScheduleByUser_UsernameAndDate(username, date);

        if (dayScheduleOptional.isPresent()) {
            return dayScheduleOptional.get();
        }

        DaySchedule schedule = createDaySchedule(username, date);

        int dayOfWeek = date.getDayOfWeek().getValue();

        var weekScheduleOptional = weekScheduleRepository
                .findByUser_UsernameAndDate(username, date.minusDays(dayOfWeek));

        WeekSchedule weekSchedule = null;
        if (weekScheduleOptional.isPresent()) {
            weekSchedule = weekScheduleOptional.get();
            weekSchedule.getDaySchedules().add(schedule);
        } else {
            weekSchedule = createWeekSchedule(username, date, schedule);
        }

        weekScheduleRepository.save(weekSchedule);

        return dayScheduleRepository.save(schedule);
    }

    private DaySchedule createDaySchedule(String username, LocalDate date) {
        DaySchedule schedule = new DaySchedule();
        schedule.setUser(userService.getUserEntityByUsername(username));
        schedule.setIsCompleted(false);
        schedule.setDate(date);

        return schedule;
    }

    private WeekSchedule createWeekSchedule(String username, LocalDate date, DaySchedule schedule) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        WeekSchedule weekSchedule = new WeekSchedule();
        weekSchedule.setUser(userService.getUserEntityByUsername(username));
        weekSchedule.getDaySchedules().add(schedule);
        weekSchedule.setDate(date.minusDays(dayOfWeek));
        schedule.setWeekSchedule(weekSchedule);
        return weekSchedule;
    }

    public void doDelete(String username, Long id) {
        dayScheduleRepository.deleteByIdAndUser_Username(id, username);
    }

    public WeeklyScheduleViewModel getWeeklySchedule(String name) {
        LocalDate monday = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue());

        int[] days = {1, 2 ,3 ,4 ,5 ,6 ,7};

        var optionalWeekSchedule = weekScheduleRepository.findByUser_UsernameAndDate(name, monday);
        Map<DayOfWeek, DailyScheduleServiceViewModel> map = new LinkedHashMap<>();
        if (optionalWeekSchedule.isPresent()) {
            var weekSchedule = optionalWeekSchedule.get();
            Arrays.stream(days).forEach(day -> {
                map.put(DayOfWeek.of(day), weekSchedule.getDaySchedules().stream()
                        .filter(daySchedule -> daySchedule.getDate().isEqual(monday.plusDays(day)))
                        .findAny()
                        .map(daySchedule -> new DailyScheduleServiceViewModel(
                                daySchedule.getWorkouts().stream().map(workout -> new WorkoutInScheduleViewModel(
                                        workout.getWorkoutDateTime(),
                                        daySchedule.getIsCompleted())).toList())).orElse(null));
            });
        } else {
            return new WeeklyScheduleViewModel(new HashMap<>());
        }
        return new WeeklyScheduleViewModel(map);
    }
}
