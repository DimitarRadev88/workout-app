package com.dimitarrradev.exercisesApi.exercise.dao;

import com.dimitarrradev.exercisesApi.exercise.Exercise;
import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    boolean existsExerciseByName(String name);

    long countAllByApprovedFalse();

    Page<Exercise> findAllByApprovedIsAndNameContainingIgnoreCase(Pageable pageable, Boolean approved, String name);

    Page<Exercise> findAllByApprovedTrueAndTargetBodyPartAndComplexityAndMovementType(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity, MovementType movementType);

    Page<Exercise> findAllByApprovedTrueAndTargetBodyPartAndComplexity(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity);

    Page<Exercise> findAllByApprovedTrueAndTargetBodyPartAndMovementType(Pageable pageable, TargetBodyPart targetBodyPart, MovementType movementType);

    Page<Exercise> findAllByApprovedTrueAndTargetBodyPart(Pageable pageable, TargetBodyPart targetBodyPart);

    Page<Exercise> findAllByApprovedTrueAndComplexityAndMovementType(Pageable pageable, Complexity complexity, MovementType movementType);

    Page<Exercise> findAllByApprovedTrueAndComplexity(Pageable pageable, Complexity complexity);

    Page<Exercise> findAllByApprovedTrueAndMovementType(Pageable pageable, MovementType movementType);

    Page<Exercise> findAllByApprovedTrue(Pageable pageable);

    List<Exercise> findAllByApprovedTrue();

    List<Exercise> findAllByApprovedTrueAndTargetBodyPartIsIn(Collection<TargetBodyPart> targetBodyParts);

    Page<Exercise> findAllByApprovedTrueAndNameContainingIgnoreCase(Pageable approved, String name);
}

