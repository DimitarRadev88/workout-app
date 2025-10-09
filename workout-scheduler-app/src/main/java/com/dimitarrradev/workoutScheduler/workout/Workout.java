package com.dimitarrradev.workoutScheduler.workout;

import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.program.Program;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.workoutExercise.WorkoutExercise;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.workout.enums.Intensity;
import com.dimitarrradev.workoutScheduler.workout.enums.Volume;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workouts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "workout_date_time")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH-mm")
    private LocalDateTime workoutDateTime;
    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutExercise> workoutExercises;
    @ManyToOne
    private Program program;
    @Basic
    @Enumerated(EnumType.STRING)
    private Intensity intensity;
    @Basic
    @Enumerated(EnumType.STRING)
    private Volume volume;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkoutType workoutType;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "day_schedule_id")
    private DaySchedule daySchedule;
    @Column(name = "target_body_parts", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<TargetBodyPart> targetBodyParts;

}
