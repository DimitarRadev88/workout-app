package com.dimitarrradev.workoutScheduler.user;

import com.dimitarrradev.workoutScheduler.BaseEntity;
import com.dimitarrradev.workoutScheduler.exercise.enums.TrainingStyle;
import com.dimitarrradev.workoutScheduler.program.Program;
import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.WeekSchedule;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Basic
    private Integer weight;
    @Basic
    private Integer height;
    @Basic
    private Integer bmi;
    @Basic
    private String gym;
    @Basic
    @Enumerated(EnumType.STRING)
    private TrainingStyle trainingStyle;
    @ManyToMany
    @JoinTable(name = "users_workouts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "workout_id")
    )
    private List<Workout> workouts;
    @ManyToMany
    @JoinTable(name = "users_programs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id")
    )
    private List<Program> programs;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
    @OneToMany(mappedBy = "user")
    private List<DaySchedule> daySchedules;
    @OneToMany(mappedBy = "user")
    private List<WeekSchedule> weekSchedules;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getBmi() {
        return bmi;
    }

    public void setBmi(Integer bmi) {
        this.bmi = bmi;
    }

    public String getGym() {
        return gym;
    }

    public void setGym(String gym) {
        this.gym = gym;
    }

    public TrainingStyle getTrainingStyle() {
        return trainingStyle;
    }

    public void setTrainingStyle(TrainingStyle trainingStyle) {
        this.trainingStyle = trainingStyle;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
