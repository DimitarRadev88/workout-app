package com.dimitarrradev.workoutScheduler.program.dao;

import com.dimitarrradev.workoutScheduler.program.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
}
