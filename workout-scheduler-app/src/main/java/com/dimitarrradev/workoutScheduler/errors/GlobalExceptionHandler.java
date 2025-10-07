package com.dimitarrradev.workoutScheduler.errors;

import com.dimitarrradev.workoutScheduler.errors.exception.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ScheduleDoesNotExistException.class)
    public String handleScheduleDoesNotExistException(Exception exception, Model model) {
        model.addAttribute("scheduleErrorMessage", exception.getMessage());
        return "schedule-not-found";
    }

    @ExceptionHandler({UsernameAlreadyExistsException.class, EmailAlreadyExistsException.class, PasswordsDoNotMatchException.class})
    public String handleUserRegistrationExceptions(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/users/register";
    }

    @ExceptionHandler(ExerciseAlreadyExistsException.class)
    public String handleExerciseAlreadyExistsException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/exercises/add";
    }

    @ExceptionHandler(ExerciseNotFoundException.class)
    public String handleExerciseNotFoundException(Exception exception, Model model) {
        model.addAttribute("error", exception.getMessage());
        return "exercise-not-found";
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public String handleInvalidPasswordException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/users/profile";
    }


}
