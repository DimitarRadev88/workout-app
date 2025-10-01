package com.dimitarrradev.workoutScheduler.util.mapping.user;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.enums.RoleType;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileAccountViewModel;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileInfoViewModel;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.dimitarrradev.workoutScheduler.RandomValueGenerator.randomWorkoutType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserToViewModelMapperUnitTests {

    @InjectMocks
    private UserToViewModelMapper mapper;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User(
                123L,
                "Test user",
                "FirstName",
                "LastName",
                "test-user@email",
                "strongpassword123",
                90,
                181,
                30,
                "Gym",
                randomWorkoutType(),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(new Role(1L, RoleType.USER, null)),
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                null
        );
    }

    @Test
    void testToUserProfileAccountViewModelReturnsModelWithCorrectData() {
        UserProfileAccountViewModel expected = new UserProfileAccountViewModel(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );

        UserProfileAccountViewModel actual = mapper.toUserProfileAccountViewModel(user);

        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    void testToUserProfileInfoViewModelReturnsModelWithCorrectDataWithWorkoutTypeNotNull() {
        UserProfileInfoViewModel expected = new UserProfileInfoViewModel(
                user.getWeight(),
                user.getHeight(),
                user.getGym(),
                user.getWorkoutType()
        );

        UserProfileInfoViewModel actual = mapper.toUserProfileInfoViewModel(user);

        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    void testToUserProfileInfoViewModelReturnsModelWithCorrectDataWithWorkoutTypeNull() {
        user.setWorkoutType(null);
        UserProfileInfoViewModel expected = new UserProfileInfoViewModel(
                user.getWeight(),
                user.getHeight(),
                user.getGym(),
                WorkoutType.CARDIO
        );

        UserProfileInfoViewModel actual = mapper.toUserProfileInfoViewModel(user);

        assertThat(actual)
                .isEqualTo(expected);
    }

}
