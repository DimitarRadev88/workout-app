package com.dimitarrradev.workoutScheduler.schedule;

import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "day_schedules")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class DaySchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private LocalDate date;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "daySchedule")
    private List<Workout> workouts;
    @Basic
    private Boolean isCompleted;
}

