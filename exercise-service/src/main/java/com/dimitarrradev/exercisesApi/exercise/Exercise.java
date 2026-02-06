package com.dimitarrradev.exercisesApi.exercise;

import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.image.ImageUrl;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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




