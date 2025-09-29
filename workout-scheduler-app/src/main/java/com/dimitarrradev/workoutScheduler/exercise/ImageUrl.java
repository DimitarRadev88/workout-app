package com.dimitarrradev.workoutScheduler.exercise;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "image_urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String url;
    @ManyToOne(fetch = FetchType.LAZY)
    private Exercise exercise;

}
