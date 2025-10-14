package com.dimitarrradev.workoutScheduler.schedule.service;

import com.dimitarrradev.workoutScheduler.errors.exception.ScheduleDoesNotExistException;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.dao.DayScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.DayScheduleServiceViewModel;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.WeekScheduleViewModel;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.user.service.UserService;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.enums.Intensity;
import com.dimitarrradev.workoutScheduler.workout.enums.Volume;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutInScheduleViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.dimitarrradev.workoutScheduler.RandomValueGenerator.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DayScheduleServiceUnitTests {

    @Mock
    private DayScheduleRepository dayScheduleRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private DayScheduleService dayScheduleService;

    private DaySchedule daySchedule;
    private User user;

    @BeforeEach
    void setUp() {
        daySchedule = new DaySchedule();
        daySchedule.setId(randomId());
        daySchedule.setDate(LocalDate.now());
        user = new User();
        user.setId(randomId());
        user.setUsername("test-user");
        daySchedule.setUser(user);
        daySchedule.setWorkouts(List.of(new Workout(
                                randomId(),
                                LocalDateTime.now(),
                                Collections.emptyList(),
                                null,
                                Intensity.MEDIUM,
                                Volume.HIGH,
                                randomWorkoutType(),
                                user,
                                daySchedule,
                                randomTargetBodyPartsList()
                        )
                )
        );
    }

    @Test
    void testGetDailyScheduleReturnsDailyScheduleServiceViewModelWithCorrectDataWhenExistsInRepository() {
        when(dayScheduleRepository.findDayScheduleByUser_UsernameAndDate(user.getUsername(), LocalDate.now()))
                .thenReturn(Optional.of(daySchedule));

        var expected = new DayScheduleServiceViewModel(
                daySchedule.getWorkouts().stream().map(workout -> new WorkoutInScheduleViewModel(
                                workout.getWorkoutDateTime(),
                                daySchedule.getIsCompleted()
                        )
                ).toList()
        );

        var actual = dayScheduleService.getDailySchedule(user.getUsername());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetDailyScheduleThrowsWhenScheduleDoesNotExistInRepository() {
        when(dayScheduleRepository.findDayScheduleByUser_UsernameAndDate(user.getUsername(), LocalDate.now()))
                .thenReturn(Optional.empty());

        assertThrows(
                ScheduleDoesNotExistException.class,
                () -> dayScheduleService.getDailySchedule(user.getUsername())
        );
    }

    @Test
    void testGetDayScheduleForDateReturnsCorrectDayScheduleWhenExistsInRepository() {
        when(dayScheduleRepository.findDayScheduleByUser_UsernameAndDate(user.getUsername(), LocalDate.now()))
                .thenReturn(Optional.of(daySchedule));

        var actual = dayScheduleService.getDayScheduleForDate(user.getUsername(), LocalDate.now());

        assertThat(actual)
                .isEqualTo(daySchedule);
    }

    @Test
    void testGetDayScheduleCreatesNewDayScheduleWhenDayScheduleDoesNotExistAndAddsItToWeekScheduleWhenWeekScheduleExistsInRepository() {
        when(dayScheduleRepository.findDayScheduleByUser_UsernameAndDate(user.getUsername(), LocalDate.now()))
                .thenReturn(Optional.empty());

        when(userService.getUserEntityByUsername(user.getUsername()))
                .thenReturn(user);

        DaySchedule schedule = new DaySchedule();
        schedule.setUser(user);
        schedule.setIsCompleted(false);
        schedule.setDate(LocalDate.now());

        dayScheduleService.getDayScheduleForDate(user.getUsername(), LocalDate.now());

        verify(
                dayScheduleRepository,
                times(1)
        ).save(schedule);
    }

    @Test
    void testGetDayScheduleCreatesNewDayScheduleWhenDayScheduleDoesNotExistAndCreatesNewWeekScheduleWhenWeekScheduleDoesNotExistInRepository() {
        LocalDate date = LocalDate.now();

        when(dayScheduleRepository.findDayScheduleByUser_UsernameAndDate(user.getUsername(), date))
                .thenReturn(Optional.empty());

        when(userService.getUserEntityByUsername(user.getUsername()))
                .thenReturn(user);

        DaySchedule schedule = new DaySchedule();
        schedule.setUser(user);
        schedule.setIsCompleted(false);
        schedule.setDate(date);

        dayScheduleService.getDayScheduleForDate(user.getUsername(), date);

        verify(
                dayScheduleRepository,
                times(1)
        ).save(schedule);

    }

    @Test
    void testDeleteDailyScheduleThrowsWhenScheduleDoesNotExistInRepository() {
        when(dayScheduleRepository.existsByIdAndUser_Username(daySchedule.getId(), user.getUsername()))
                .thenReturn(false);

        assertThrows(
                ScheduleDoesNotExistException.class,
                () -> dayScheduleService.deleteDailySchedule(user.getUsername(), daySchedule.getId())
        );

        verify(dayScheduleRepository, never())
                .deleteByIdAndUser_Username(daySchedule.getId(), user.getUsername());
    }

    @Test
    void testDeleteDailyScheduleDeletesExistingDaySchedule() {
        when(dayScheduleRepository.existsByIdAndUser_Username(daySchedule.getId(), user.getUsername()))
                .thenReturn(true);

        dayScheduleService.deleteDailySchedule(user.getUsername(), daySchedule.getId());

        verify(
                dayScheduleRepository,
                times(1)
        ).deleteByIdAndUser_Username(daySchedule.getId(), user.getUsername());
    }

    @Test
    void testGetWeekScheduleReturnsCorrectWeekScheduleWhenExistsInRepository() {
        LocalDate monday = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);

        List<DaySchedule> schedules = new ArrayList<>(List.of(daySchedule));

        when(dayScheduleRepository
                .findAllByUser_UsernameAndDateIsBetweenOrderByDateAsc(user.getUsername(), monday, monday.plusDays(6)))
                .thenReturn(schedules);

        Map<DayOfWeek, List<DayScheduleServiceViewModel>> map = new LinkedHashMap<>();

        Arrays
                .stream(DayOfWeek.values())
                .forEach(day -> map.put(day, new ArrayList<>()));

        schedules
                .forEach(schedule -> {
                    map.get(schedule.getDate().getDayOfWeek())
                            .add(new DayScheduleServiceViewModel(
                                    schedule.getWorkouts()
                                            .stream()
                                            .map(workout -> new WorkoutInScheduleViewModel(workout.getWorkoutDateTime(), false))
                                            .toList()));
                });

        var expected = new WeekScheduleViewModel(map);

        var actual = dayScheduleService.getWeekSchedule(user.getUsername());

        assertThat(actual)
                .isEqualTo(expected);

    }

}
