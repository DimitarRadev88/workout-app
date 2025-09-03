package com.dimitarrradev.workoutScheduler.exercise.dao;

import com.dimitarrradev.workoutScheduler.exercise.ImageUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageUrlDao extends JpaRepository<ImageUrl, Long> {
    List<ImageUrl> findByExercise_Id(Long exerciseId);
}
