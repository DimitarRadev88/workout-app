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

    List<Exercise> findAllByTargetBodyPartIsInAndIsDeletedFalse(Collection<TargetBodyPart> targetBodyParts);

    Page<Exercise> findAllByTargetBodyPartAndMovementTypeAndIsDeletedFalse(Pageable pageable, TargetBodyPart target, MovementType movement);

    Page<Exercise> findAllByTargetBodyPartAndComplexityAndIsDeletedFalse(Pageable pageable, TargetBodyPart target, Complexity complexity);

    Page<Exercise> findAllByComplexityAndMovementTypeAndIsDeletedFalse(Pageable pageable, Complexity complexity, MovementType movement);

    Page<Exercise> findAllByTargetBodyPartAndComplexityAndMovementTypeAndIsDeletedFalse(Pageable pageable, TargetBodyPart target, Complexity complexity, MovementType movement);

    Page<Exercise> findAllByTargetBodyPartAndIsDeletedFalse(Pageable pageable, TargetBodyPart target);

    Page<Exercise> findAllByComplexityAndIsDeletedFalse(Pageable pageable, Complexity complexity);

    Page<Exercise> findAllByMovementTypeAndIsDeletedFalse(Pageable pageable, MovementType movement);

    Page<Exercise> findAllByIsDeletedFalse(Pageable pageable);

    Page<Exercise> findAllByNameContainingIgnoreCaseAndIsDeletedFalse(Pageable pageable, String name);
}

