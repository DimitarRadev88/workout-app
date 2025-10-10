package com.dimitarrradev.workoutScheduler.schedule.dao;

import com.dimitarrradev.workoutScheduler.schedule.WeekSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeekScheduleRepository extends JpaRepository<WeekSchedule, Long> {

    Optional<WeekSchedule> findByUser_UsernameAndDate(String userUsername, LocalDate date);

    boolean existsByUser_UsernameAndDate(String username, LocalDate minus);
}
