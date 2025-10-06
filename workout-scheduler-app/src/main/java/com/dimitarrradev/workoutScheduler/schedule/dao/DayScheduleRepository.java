package com.dimitarrradev.workoutScheduler.schedule.dao;

import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DayScheduleRepository extends JpaRepository<DaySchedule, Long> {

    Optional<DaySchedule> findDayScheduleByUser_UsernameAndDate(String username, LocalDate date);

    void deleteByIdAndUser_Username(Long id, String username);
}
