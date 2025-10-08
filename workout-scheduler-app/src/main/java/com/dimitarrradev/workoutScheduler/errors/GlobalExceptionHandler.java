package com.dimitarrradev.workoutScheduler.errors;

import com.dimitarrradev.workoutScheduler.errors.exception.*;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ScheduleDoesNotExistException.class)
    public String handleScheduleDoesNotExistException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/schedules";
    }

    @ExceptionHandler({UsernameAlreadyExistsException.class, EmailAlreadyExistsException.class, PasswordsDoNotMatchException.class})
    public String handleUserRegistrationExceptions(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/users/register";
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public String handleInvalidPasswordException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/users/profile";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(Exception exception, Authentication authentication, RedirectAttributes redirectAttributes) {
        authentication.setAuthenticated(false);
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/users/login";
    }

    @ExceptionHandler(ExerciseAlreadyExistsException.class)
    public String handleExerciseAlreadyExistsException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/exercises/add";
    }

    @ExceptionHandler(ExerciseNotFoundException.class)
    public String handleExerciseNotFoundException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/exercises";
    }

    @ExceptionHandler(WorkoutNotFoundException.class)
    public String handleWorkoutNotFoundException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/workouts";
    }

}
