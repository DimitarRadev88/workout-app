package com.dimitarrradev.workoutScheduler.exercise;

import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
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
    @Basic
    private Boolean approved;
    @Column(nullable = false, name = "added_by")
    private String addedBy;
    @Basic
    @Enumerated(EnumType.STRING)
    private Complexity complexity;
    @Basic
    private String equipment;

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

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public Complexity getComplexity() {
        return complexity;
    }

    public void setComplexity(Complexity complexity) {
        this.complexity = complexity;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
}
