package com.dimitarrradev.exercisesApi.exercise.dao;

import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.exercise.model.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    boolean existsExerciseByNameAndIsDeletedFalse(String name);

    List<Exercise> findAllByTargetBodyPartIsInAndApprovedTrueAndIsDeletedFalse(Collection<TargetBodyPart> targetBodyParts);

    Page<Exercise> findAllByTargetBodyPartAndMovementTypeAndApprovedTrueAndIsDeletedFalse(Pageable pageable, TargetBodyPart target, MovementType movement);

    Page<Exercise> findAllByTargetBodyPartAndComplexityAndApprovedTrueAndIsDeletedFalse(Pageable pageable, TargetBodyPart target, Complexity complexity);

    Page<Exercise> findAllByComplexityAndMovementTypeAndApprovedTrueAndIsDeletedFalse(Pageable pageable, Complexity complexity, MovementType movement);

    Page<Exercise> findAllByTargetBodyPartAndComplexityAndMovementTypeAndApprovedTrueAndIsDeletedFalse(Pageable pageable, TargetBodyPart target, Complexity complexity, MovementType movement);

    Page<Exercise> findAllByTargetBodyPartAndApprovedTrueAndIsDeletedFalse(Pageable pageable, TargetBodyPart target);

    Page<Exercise> findAllByComplexityAndApprovedTrueAndIsDeletedFalse(Pageable pageable, Complexity complexity);

    Page<Exercise> findAllByMovementTypeAndApprovedTrueAndIsDeletedFalse(Pageable pageable, MovementType movement);

    Page<Exercise> findAllByApprovedTrueAndIsDeletedFalse(Pageable pageable);

    Page<Exercise> findAllByNameContainingIgnoreCaseAndApprovedTrueAndIsDeletedFalse(Pageable pageable, String name);

    Page<Exercise> findAllByApprovedFalseAndIsDeletedFalse(Pageable pageable);

    long countByApprovedFalseAndIsDeletedFalse();
}

