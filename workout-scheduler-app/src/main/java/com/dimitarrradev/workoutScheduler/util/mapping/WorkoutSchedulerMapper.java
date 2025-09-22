package com.dimitarrradev.workoutScheduler.util.mapping;

public interface WorkoutSchedulerMapper<T> {

    <E> T mapFrom(E object);

}
