package com.dimitarrradev.workoutScheduler.user;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.enums.RoleType;
import com.dimitarrradev.workoutScheduler.role.service.RoleService;
import com.dimitarrradev.workoutScheduler.user.dao.UserRepository;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileAccountViewModel;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileInfoViewModel;
import com.dimitarrradev.workoutScheduler.user.service.UserService;
import com.dimitarrradev.workoutScheduler.web.binding.UserRegisterBindingModel;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserCountShouldReturnCorrectCountOfUsersInRepository() {
        when(userRepository.count())
                .thenReturn(5L);

        assertThat(userService.getUserCount())
                .isEqualTo(5L);
    }

    @Test
    void testDoRegisterThrowsWhenUserWithUsernameAlreadyExistsInRepository() {
        when(userRepository.existsUserByUsername("existing"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.doRegister(
                        new UserRegisterBindingModel("existing", null, null, null)
                ));
    }

    @Test
    void testDoRegisterThrowsWhenUserWithEmailAlreadyExistsInRepository() {
        when(userRepository.existsUserByEmail("existing@email"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.doRegister(
                        new UserRegisterBindingModel("valid username", "existing@email", null, null)
                ));
    }

    @Test
    void testDoRegisterCreatesAndSavesNewUserWithCorrectData() {
        UserRegisterBindingModel bindingModel = new UserRegisterBindingModel("valid username", "valid@email", "validPassword", "validPassword");
        String encodedPassword = "" + bindingModel.password().hashCode() * 0.98765432123456789;

        when(userRepository.existsUserByUsername(bindingModel.username()))
                .thenReturn(false);
        when(userRepository.existsUserByEmail(bindingModel.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(bindingModel.password()))
                .thenReturn(encodedPassword);

        Role userRole = new Role(1L, RoleType.USER, new ArrayList<>());

        when(roleService.getRoleByType(RoleType.USER))
                .thenReturn(userRole);

        User expected = new User();
        expected.setUsername(bindingModel.username());
        expected.setEmail(bindingModel.email());
        expected.setPassword(encodedPassword);
        expected.setRoles(new ArrayList<>(List.of(userRole)));

        when(userRepository.save(expected))
                .thenReturn(expected);

        userService.doRegister(bindingModel);

        verify(userRepository, times(1))
                .save(expected);
    }

    @Test
    void testGetUserProfileAccountViewReturnsCorrectUserProfileAccountViewModel() {
        User user = new User();
        user.setUsername("username");
        user.setEmail("email");
        user.setFirstName("firstName");
        user.setLastName("lastName");

        UserProfileAccountViewModel expected = new UserProfileAccountViewModel(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName());

        when(userRepository.findUserByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        assertThat(userService.getUserProfileAccountView(user.getUsername()))
                .isEqualTo(expected);
    }

    @Test
    void testGetUserProfileAccountViewThrowsWhenUserNotFound() {
        when(userRepository.findUserByUsername("not existing"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserProfileAccountView("not existing"));
    }

    @Test
    void testGetUserProfileInfoViewWithSetWorkoutTypeReturnsCorrectProfileInfoViewModel() {
        User user = new User();
        user.setWeight(90);
        user.setHeight(180);
        user.setGym("Gym");
        user.setWorkoutType(WorkoutType.BODYBUILDING);

        when(userRepository.findUserByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        UserProfileInfoViewModel expected = new UserProfileInfoViewModel(
                user.getWeight(),
                user.getHeight(),
                user.getGym(),
                user.getWorkoutType()
        );

        assertThat(userService.getUserProfileInfoView(user.getUsername()))
                .isEqualTo(expected);
    }

    @Test
    void testGetUserProfileInfoViewWithoutSetWorkoutTypeReturnsCorrectProfileInfoViewModel() {
        User user = new User();
        user.setWeight(90);
        user.setHeight(180);
        user.setGym("Gym");

        when(userRepository.findUserByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        UserProfileInfoViewModel expected = new UserProfileInfoViewModel(
                user.getWeight(),
                user.getHeight(),
                user.getGym(),
                WorkoutType.CARDIO
        );

        assertThat(userService.getUserProfileInfoView(user.getUsername()))
                .isEqualTo(expected);
    }

    @Test
    void testGetUserProfileInfoViewThrowsWhenUserNotFound() {
        when(userRepository.findUserByUsername("not existing"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserProfileInfoView("not existing"));
    }


}
