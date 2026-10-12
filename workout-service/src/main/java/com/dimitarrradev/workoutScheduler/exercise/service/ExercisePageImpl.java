package com.dimitarrradev.workoutScheduler.exercise.service;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class ExercisePageImpl<T> extends PageImpl<T> {

    public ExercisePageImpl(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public ExercisePageImpl(List<T> content) {
        super(content);
    }

    public ExercisePageImpl() {
        super(new ArrayList<>());
    }
}
