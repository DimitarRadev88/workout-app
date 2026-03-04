package com.scheduleService.schedule.model;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @ElementCollection(targetClass = Long.class, fetch = FetchType.EAGER)
    @MapKeyColumn(name = "exercise_id")
    @Column(name = "reps")
    @CollectionTable(name = "exercises_reps", joinColumns = @JoinColumn(name = "schedule_id"))
    private Map<Long, Long> exercises;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

}
