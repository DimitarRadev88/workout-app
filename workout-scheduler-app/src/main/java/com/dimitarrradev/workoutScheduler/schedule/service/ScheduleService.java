package com.dimitarrradev.workoutScheduler.schedule.service;

import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.dao.DayScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.dao.WeekScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.DailyScheduleServiceViewModel;
import com.dimitarrradev.workoutScheduler.user.service.UserService;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutInScheduleViewModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

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
                .orElseThrow(() -> new IllegalArgumentException("No schedule found"));

        return new DailyScheduleServiceViewModel(
                daySchedule.getWorkouts().stream().map(workout -> new WorkoutInScheduleViewModel(
                        workout.getWorkoutDateTime(),
                        daySchedule.getIsCompleted()
                )).toList()
        );
    }

    public DaySchedule getDayScheduleForDate(String username, LocalDate localDate) {
        Optional<DaySchedule> daySchedule = dayScheduleRepository.findDayScheduleByUser_UsernameAndDate(username, localDate);

        if (daySchedule.isPresent()) {
            return daySchedule.get();
        }

        DaySchedule schedule = new DaySchedule();
        schedule.setUser(userService.getUserEntityByUsername(username));
        schedule.setIsCompleted(false);
        schedule.setDate(localDate);

        return dayScheduleRepository.save(schedule);
    }

    public void doDelete(String username, Long id) {
        dayScheduleRepository.deleteByIdAndUser_Username(id, username);
    }
}
