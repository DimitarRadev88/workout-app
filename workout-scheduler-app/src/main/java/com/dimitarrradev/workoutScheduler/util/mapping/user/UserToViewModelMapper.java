package com.dimitarrradev.workoutScheduler.util.mapping.user;

import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileAccountViewModel;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileInfoViewModel;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserToViewModelMapper {

    public UserProfileAccountViewModel toUserProfileAccountViewModel(User user) {
        return new UserProfileAccountViewModel(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public UserProfileInfoViewModel toUserProfileInfoViewModel(User user) {
        return new UserProfileInfoViewModel(
                user.getWeight(),
                user.getHeight(),
                user.getGym(),
                user.getWorkoutType() != null ? user.getWorkoutType() : WorkoutType.CARDIO
        );
    }

}
