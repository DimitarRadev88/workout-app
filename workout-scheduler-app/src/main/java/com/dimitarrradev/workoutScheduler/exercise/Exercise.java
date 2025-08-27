package com.dimitarrradev.workoutScheduler.exercise;

import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import jakarta.persistence.*;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, name = "target_body_part")
    @Enumerated(EnumType.STRING)
    private TargetBodyPart targetBodyPart;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Basic
    private String picture;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TargetBodyPart getTargetBodyPart() {
        return targetBodyPart;
    }

    public void setTargetBodyPart(TargetBodyPart targetBodyPart) {
        this.targetBodyPart = targetBodyPart;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
