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
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    boolean existsExerciseByName(String name);

    Page<Exercise> findAllBy(Pageable pageable);

    Page<Exercise> findAllByTargetBodyPart(Pageable pageable, TargetBodyPart targetBodyPart);

    List<Exercise> findAllByTargetBodyPartIsIn(Collection<TargetBodyPart> targetBodyParts);

    Page<Exercise> findAllByComplexity(Pageable pageable, Complexity complexity);

    Page<Exercise> findAllByMovementType(Pageable pageable, MovementType movementType);

    Page<Exercise> findAllByNameContainingIgnoreCase(Pageable pageable, String name);

    Page<Exercise> findAllByTargetBodyPartAndComplexityAndMovementType(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity, MovementType movementType);

    Page<Exercise> findAllByTargetBodyPartAndComplexity(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity);

    Page<Exercise> findAllByTargetBodyPartAndMovementType(Pageable pageable, TargetBodyPart targetBodyPart, MovementType movementType);

    Page<Exercise> findAllByComplexityAndMovementType(Pageable pageable, Complexity complexity, MovementType movementType);

    Optional<Exercise> findByName(String name);
}

