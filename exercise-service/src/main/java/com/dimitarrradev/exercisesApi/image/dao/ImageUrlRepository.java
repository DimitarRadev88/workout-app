package com.dimitarrradev.exercisesApi.image.dao;

import com.dimitarrradev.exercisesApi.image.ImageUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageUrlRepository extends JpaRepository<ImageUrl, Long> {
    List<ImageUrl> findByExercise_Id(Long exerciseId);
}
