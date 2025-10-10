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
        Optional<DaySchedule> daySchedule = dayScheduleRepository.findDayScheduleByUser_UsernameAndDate(username, date);

        if (daySchedule.isPresent()) {
            return daySchedule.get();
        }

        DaySchedule schedule = new DaySchedule();
        schedule.setUser(userService.getUserEntityByUsername(username));
        schedule.setIsCompleted(false);
        schedule.setDate(date);

        int dayOfWeek = date.getDayOfWeek().getValue();

        WeekSchedule weekSchedule = null;
        if (!weekScheduleRepository.existsByUser_UsernameAndDate(username, date.minusDays(dayOfWeek))) {
            weekSchedule = new WeekSchedule();
            weekSchedule.setUser(userService.getUserEntityByUsername(username));
            weekSchedule.getDaySchedules().add(schedule);
            weekSchedule.setDate(date.minusDays(dayOfWeek));
            schedule.setWeekSchedule(weekSchedule);
            weekScheduleRepository.save(weekSchedule);

        } else {
            weekSchedule = weekScheduleRepository.findByUser_UsernameAndDate(username, date).get();
            weekSchedule.getDaySchedules().add(schedule);
            weekScheduleRepository.save(weekSchedule);
        }

        return dayScheduleRepository.save(schedule);
    }

    public void doDelete(String username, Long id) {
        dayScheduleRepository.deleteByIdAndUser_Username(id, username);
    }

    public WeeklyScheduleViewModel getWeeklySchedule(String name) {
        LocalDate monday = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue());

        int[] days = {1, 2 ,3 ,4 ,5 ,6 ,7};

        Optional<WeekSchedule> optionalWeekSchedule = weekScheduleRepository.findByUser_UsernameAndDate(name, monday);
        Map<DayOfWeek, DailyScheduleServiceViewModel> map = new LinkedHashMap<>();
        if (optionalWeekSchedule.isPresent()) {
            WeekSchedule weekSchedule = optionalWeekSchedule.get();
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
