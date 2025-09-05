package com.dimitarrradev.workoutScheduler.schedule.dao;

import com.dimitarrradev.workoutScheduler.schedule.WeekSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeekScheduleRepository extends JpaRepository<WeekSchedule, Long> {

}
