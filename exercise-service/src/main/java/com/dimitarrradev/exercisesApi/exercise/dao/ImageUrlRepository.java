package com.dimitarrradev.exercisesApi.exercise.dao;

import com.dimitarrradev.exercisesApi.exercise.model.ImageUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageUrlRepository extends JpaRepository<ImageUrl, Long> {
    List<ImageUrl> findByExercise_Id(Long exerciseId);

    Optional<ImageUrl> findByIdAndExercise_id(Long imageId, Long id);

}
