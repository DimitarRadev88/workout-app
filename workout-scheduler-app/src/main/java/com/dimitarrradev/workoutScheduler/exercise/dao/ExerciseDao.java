package com.dimitarrradev.workoutScheduler.exercise.dao;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseDao extends JpaRepository<Exercise, Long> {
    boolean existsExerciseByName(String name);

    long countAllByApprovedFalse();

    Page<Exercise> findAllByApprovedIs(Pageable pageable, Boolean approved);

    Page<Exercise> findAllByApprovedTrueAndTargetBodyPartAndComplexity(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity);

    Page<Exercise> findAllByApprovedTrueAndTargetBodyPart(Pageable pageable, TargetBodyPart targetBodyPart);

    Page<Exercise> findAllByApprovedTrueAndComplexity(Pageable pageable, Complexity complexity);
}
