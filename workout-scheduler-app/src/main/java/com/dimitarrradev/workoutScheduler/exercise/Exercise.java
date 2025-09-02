package com.dimitarrradev.workoutScheduler.exercise;

import com.dimitarrradev.workoutScheduler.BaseEntity;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises")
public class Exercise extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, name = "target_body_part")
    @Enumerated(EnumType.STRING)
    private TargetBodyPart targetBodyPart;
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
    @Basic
    private String equipment;

    public Exercise() {
        this.imageURLs = new ArrayList<>();
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

    public List<ImageUrl> getImageURLs() {
        return imageURLs;
    }

    public void setImageURLs(List<ImageUrl> imageURLs) {
        this.imageURLs = imageURLs;
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
