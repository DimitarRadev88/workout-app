package com.dimitarrradev.workoutScheduler.training.dao;

import com.dimitarrradev.workoutScheduler.training.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramDao extends JpaRepository<Program, Long> {
}
