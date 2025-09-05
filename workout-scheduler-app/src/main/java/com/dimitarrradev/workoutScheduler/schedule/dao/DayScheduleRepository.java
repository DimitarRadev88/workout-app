package com.dimitarrradev.workoutScheduler.schedule.dao;

import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayScheduleRepository extends JpaRepository<DaySchedule, Long> {

}
