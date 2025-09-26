package com.dimitarrradev.workoutScheduler.user;

import com.dimitarrradev.workoutScheduler.program.Program;
import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.schedule.WeekSchedule;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    private WorkoutType workoutType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
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

}
