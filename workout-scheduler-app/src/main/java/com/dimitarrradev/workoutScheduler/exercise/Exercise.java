package com.dimitarrradev.workoutScheduler.exercise;

import com.dimitarrradev.workoutScheduler.BaseEntity;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, name = "target_body_part")
    @Enumerated(EnumType.STRING)
    private TargetBodyPart targetBodyPart;
    @Column(nullable = false, name = "movement_type")
    @Enumerated(EnumType.STRING)
    private MovementType movementType;
    @Column(columnDefinition = "TEXT")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise", fetch = FetchType.LAZY)
    private List<ImageUrl> imageURLs;
    @Basic
    private Boolean approved;
    @Column(nullable = false, name = "added_by")
    private String addedBy;
    @Basic
    @Enumerated(EnumType.STRING)
    private Complexity complexity;

}




