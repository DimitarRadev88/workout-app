package com.dimitarrradev.workoutScheduler.program;

import com.dimitarrradev.workoutScheduler.program.enums.ProgramGoal;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Program {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String title;
    @Column(unique = true, nullable = false)
    private Integer duration;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false, name = "program_goal")
    @Enumerated(EnumType.STRING)
    private ProgramGoal programGoal;
    @Column(nullable = false, name = "workouts_per_week")
    private Integer workoutsPerWeek;
    @OneToMany
    private List<Workout> workouts;
    @ManyToMany(mappedBy = "programs")
    private List<User> users;

}
