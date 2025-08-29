package com.dimitarrradev.workoutScheduler.exercise.dao;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseDao extends JpaRepository<Exercise, Long> {
    boolean existsExerciseByName(String name);

    long countAllByActiveIsFalse();

    Page<Exercise> findAllByActiveFalse(Pageable pageable);
}
