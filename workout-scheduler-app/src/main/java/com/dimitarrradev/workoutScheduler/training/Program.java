package com.dimitarrradev.workoutScheduler.training;

import com.dimitarrradev.workoutScheduler.training.enums.ProgramGoal;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "programs")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private String title;
    @Column(unique = true, nullable = false)
    private Integer duration;
    @Column(nullable = false, name = "program_goal")
    @Enumerated(EnumType.STRING)
    private ProgramGoal programGoal;
    @Column(nullable = false, name = "workouts_per_week")
    private Integer workoutsPerWeek;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Workout> workouts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ProgramGoal getProgramGoal() {
        return programGoal;
    }

    public void setProgramGoal(ProgramGoal programGoal) {
        this.programGoal = programGoal;
    }

    public Integer getWorkoutsPerWeek() {
        return workoutsPerWeek;
    }

    public void setWorkoutsPerWeek(Integer workoutsPerWeek) {
        this.workoutsPerWeek = workoutsPerWeek;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }
}
