package com.dimitarrradev.exercisesApi.error.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class InvalidRequestBodyException extends RuntimeException {
    private final BindingResult bindingResult;
    public InvalidRequestBodyException(BindingResult bindingResult) {
        super();
        this.bindingResult = bindingResult;
    }

}