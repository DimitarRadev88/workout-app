package com.dimitarrradev.workoutScheduler.exercise.dao;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    boolean existsExerciseByName(String name);

    long countAllByApprovedFalse();

    Page<Exercise> findAllByApprovedIsAndNameContainingIgnoreCase(Pageable pageable, Boolean approved, String name);

    Page<Exercise> findAllByApprovedTrueAndTargetBodyPartAndComplexityAndNameContainingIgnoreCase(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity, String name);

    Page<Exercise> findAllByApprovedTrueAndTargetBodyPartAndNameContainingIgnoreCase(Pageable pageable, TargetBodyPart targetBodyPart, String name);

    Page<Exercise> findAllByApprovedTrueAndComplexityAndNameContainingIgnoreCase(Pageable pageable, Complexity complexity, String name);

    Page<Exercise> findAllByApprovedTrueAndTargetBodyPartAndComplexityAndMovementTypeAndNameContainingIgnoreCase(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity, MovementType movementType, String name);

    Page<Exercise> findAllByApprovedTrueAndTargetBodyPartAndMovementTypeAndNameContainingIgnoreCase(Pageable pageable, TargetBodyPart targetBodyPart, MovementType movementType, String trim);

    Page<Exercise> findAllByApprovedTrueAndComplexityAndMovementTypeAndNameContainingIgnoreCase(Pageable pageable, Complexity complexity, MovementType movementType, String trim);

    Page<Exercise> findAllByApprovedTrueAndMovementTypeAndNameContainingIgnoreCase(Pageable pageable, MovementType movementType, String trim);

    Page<Exercise> findAllByApprovedTrueAndNameContainingIgnoreCase(Pageable pageable, String trim);
}

