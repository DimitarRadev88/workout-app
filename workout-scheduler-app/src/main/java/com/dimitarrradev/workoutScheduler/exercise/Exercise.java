package com.dimitarrradev.workoutScheduler.exercise;

import com.dimitarrradev.workoutScheduler.BaseEntity;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises")
public record Exercise(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,
        @Column(nullable = false, unique = true)
        String name,
        @Column(nullable = false, name = "target_body_part")
        @Enumerated(EnumType.STRING)
        TargetBodyPart targetBodyPart,
        @Column(nullable = false, name = "movement_type")
        @Enumerated(EnumType.STRING)
        MovementType movementType,
        @Column(columnDefinition = "TEXT")
        String description,
        @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise", fetch = FetchType.LAZY)
        List<ImageUrl> imageURLs,
        @Basic
        Boolean approved,
        @Column(nullable = false, name = "added_by")
        String addedBy,
        @Basic
        @Enumerated(EnumType.STRING)
        Complexity complexity
) {
}
