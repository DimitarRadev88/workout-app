package com.dimitarrradev.workoutScheduler.schedule.service;

import com.dimitarrradev.workoutScheduler.errors.exception.ScheduleDoesNotExistException;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.WeekSchedule;
import com.dimitarrradev.workoutScheduler.schedule.dao.DayScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.dao.WeekScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.dto.WeeklyScheduleViewModel;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.DailyScheduleServiceViewModel;
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
import java.util.stream.IntStream;

import static com.dimitarrradev.workoutScheduler.RandomValueGenerator.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceUnitTests {

    @Mock
    private DayScheduleRepository dayScheduleRepository;
    @Mock
    private WeekScheduleRepository weekScheduleRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ScheduleService scheduleService;

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

        var expected = new DailyScheduleServiceViewModel(
                daySchedule.getWorkouts().stream().map(workout -> new WorkoutInScheduleViewModel(
                                workout.getWorkoutDateTime(),
                                daySchedule.getIsCompleted()
                        )
                ).toList()
        );

        var actual = scheduleService.getDailySchedule(user.getUsername());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetDailyScheduleThrowsWhenScheduleDoesNotExistInRepository() {
        when(dayScheduleRepository.findDayScheduleByUser_UsernameAndDate(user.getUsername(), LocalDate.now()))
                .thenReturn(Optional.empty());

        assertThrows(
                ScheduleDoesNotExistException.class,
                () -> scheduleService.getDailySchedule(user.getUsername())
        );
    }

    @Test
    void testGetDayScheduleForDateReturnsCorrectDayScheduleWhenExistsInRepository() {
        when(dayScheduleRepository.findDayScheduleByUser_UsernameAndDate(user.getUsername(), LocalDate.now()))
                .thenReturn(Optional.of(daySchedule));

        var actual = scheduleService.getDayScheduleForDate(user.getUsername(), LocalDate.now());

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

        WeekSchedule weekSchedule = new WeekSchedule();
        weekSchedule.setId(randomId());
        weekSchedule.setDate(LocalDate.now());

        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();

        when(weekScheduleRepository.findByUser_UsernameAndDate(user.getUsername(), LocalDate.now().minusDays(dayOfWeek - 1)))
                .thenReturn(Optional.of(weekSchedule));

        scheduleService.getDayScheduleForDate(user.getUsername(), LocalDate.now());

        assertThat(weekSchedule.getDaySchedules().size())
                .isEqualTo(1);

        verify(
                weekScheduleRepository,
                times(1)
        ).save(weekSchedule);

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

        int dayOfWeek = date.getDayOfWeek().getValue();

        WeekSchedule weekSchedule = new WeekSchedule();
        weekSchedule.setUser(userService.getUserEntityByUsername(user.getUsername()));
        weekSchedule.getDaySchedules().add(schedule);
        weekSchedule.setDate(date.minusDays(dayOfWeek - 1));
        schedule.setWeekSchedule(weekSchedule);

        when(weekScheduleRepository.findByUser_UsernameAndDate(user.getUsername(), date.minusDays(dayOfWeek - 1)))
                .thenReturn(Optional.empty());

        scheduleService.getDayScheduleForDate(user.getUsername(), date);

        assertThat(weekSchedule.getDaySchedules().getFirst())
                .isEqualTo(schedule);

        verify(
                weekScheduleRepository,
                times(1)
        ).save(any(WeekSchedule.class));

        verify(
                dayScheduleRepository,
                times(1)
        ).save(any(DaySchedule.class));

    }

    @Test
    void testDeleteDailyScheduleThrowsWhenScheduleDoesNotExistInRepository() {
        when(dayScheduleRepository.existsByIdAndUser_Username(daySchedule.getId(), user.getUsername()))
                .thenReturn(false);

        assertThrows(
                ScheduleDoesNotExistException.class,
                () -> scheduleService.deleteDailySchedule(user.getUsername(), daySchedule.getId())
        );

        verify(dayScheduleRepository, never())
                .deleteByIdAndUser_Username(daySchedule.getId(), user.getUsername());
    }

    @Test
    void testDeleteDailyScheduleDeletesExistingDaySchedule() {
        when(dayScheduleRepository.existsByIdAndUser_Username(daySchedule.getId(), user.getUsername()))
                .thenReturn(true);

        scheduleService.deleteDailySchedule(user.getUsername(), daySchedule.getId());

        verify(
                dayScheduleRepository,
                times(1)
        ).deleteByIdAndUser_Username(daySchedule.getId(), user.getUsername());
    }

    @Test
    void testGetWeeklyScheduleReturnsCorrectWeekScheduleWhenExistsInRepository() {
        WeekSchedule weekSchedule = new WeekSchedule();
        weekSchedule.setId(randomId());
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        LocalDate monday = LocalDate.now().minusDays(dayOfWeek.getValue() - 1);
        weekSchedule.setDaySchedules(new ArrayList<>(List.of(daySchedule)));
        weekSchedule.setDate(monday);
        when(weekScheduleRepository.findByUser_UsernameAndDate(user.getUsername(), LocalDate.now()))
                .thenReturn(Optional.of(weekSchedule));

        Map<DayOfWeek, DailyScheduleServiceViewModel> map = new LinkedHashMap<>();
        IntStream
                .rangeClosed(1, 7)
                .forEach(i -> {
                    map.put(DayOfWeek.of(i), null);
                });
        map.put(dayOfWeek, new DailyScheduleServiceViewModel(
                daySchedule.getWorkouts().stream().map(workout -> new WorkoutInScheduleViewModel(
                        workout.getWorkoutDateTime(),
                        daySchedule.getIsCompleted())
                ).toList())
        );
        var expected = new WeeklyScheduleViewModel(map);

        var actual = scheduleService.getWeeklySchedule(user.getUsername());

        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    void testGetWeeklyScheduleThrowsWhenScheduleDoesNotExistInRepository() {
        LocalDate monday = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        when(weekScheduleRepository.findByUser_UsernameAndDate(user.getUsername(), monday))
                .thenReturn(Optional.empty());

        assertThrows(
                ScheduleDoesNotExistException.class, () ->
                scheduleService.getWeeklySchedule(user.getUsername())
        );
    }


}
