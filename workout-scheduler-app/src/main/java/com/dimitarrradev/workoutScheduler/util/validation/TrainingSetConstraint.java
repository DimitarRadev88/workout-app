package com.dimitarrradev.workoutScheduler.util.validation;

import com.dimitarrradev.workoutScheduler.web.binding.ExerciseTrainingSetsBindingModel;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExerciseTrainingSetsBindingModelConstraintValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TrainingSetConstraint {
    String message() default "Invalid exercise sets information";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
