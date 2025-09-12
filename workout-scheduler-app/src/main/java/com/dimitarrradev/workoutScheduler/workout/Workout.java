package com.dimitarrradev.workoutScheduler.workout;

import com.dimitarrradev.workoutScheduler.BaseEntity;
import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.program.Program;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.workout.enums.Intensity;
import com.dimitarrradev.workoutScheduler.workout.enums.Volume;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "workouts")
public class Workout extends BaseEntity {
    @Column(nullable = false, name = "workout_date_time")
    @FutureOrPresent
    @DateTimeFormat(pattern = "dd-MM-yyyy HH-mm")
    private LocalDateTime workoutDateTime;
    @OneToMany
    @MapKeyJoinColumn(name = "exercise_id")
    @JoinColumn(name = "set_id")
    @JoinTable(
            name = "workouts_exercises",
            joinColumns = @JoinColumn(name = "workout_id")
    )
    private Map<Exercise, TrainingSet> exerciseTrainingSets;
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

    public LocalDateTime getWorkoutDateTime() {
        return workoutDateTime;
    }

    public void setWorkoutDateTime(LocalDateTime workoutDateTime) {
        this.workoutDateTime = workoutDateTime;
    }

    public Map<Exercise, TrainingSet> getExerciseTrainingSets() {
        return exerciseTrainingSets;
    }

    public void setExerciseTrainingSets(Map<Exercise, TrainingSet> exerciseTrainingSets) {
        this.exerciseTrainingSets = exerciseTrainingSets;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Intensity getIntensity() {
        return intensity;
    }

    public void setIntensity(Intensity intensity) {
        this.intensity = intensity;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DaySchedule getDaySchedule() {
        return daySchedule;
    }

    public void setDaySchedule(DaySchedule daySchedule) {
        this.daySchedule = daySchedule;
    }

    public List<TargetBodyPart> getTargetBodyParts() {
        return targetBodyParts;
    }

    public void setTargetBodyParts(List<TargetBodyPart> targetBodyParts) {
        this.targetBodyParts = targetBodyParts;
    }
}
