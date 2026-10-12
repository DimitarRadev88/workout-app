package com.scheduleService.schedule.dao;

import com.scheduleService.schedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByUserIdAndDate(Long userId, LocalDate date);

    List<Schedule> findAllByUserId(Long userId);

    boolean existsByDateAndUserId(LocalDate date, Long aLong);
}
