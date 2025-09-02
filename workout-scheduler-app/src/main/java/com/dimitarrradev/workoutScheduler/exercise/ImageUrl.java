package com.dimitarrradev.workoutScheduler.exercise;

import com.dimitarrradev.workoutScheduler.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "image_urls")
public class ImageUrl extends BaseEntity {

    @Column(nullable = false)
    private String url;
    @ManyToOne(fetch = FetchType.LAZY)
    private Exercise exercise;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }
}
