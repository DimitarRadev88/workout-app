package com.dimitarrradev.workoutScheduler.user.service;

import com.dimitarrradev.workoutScheduler.errors.exception.*;
import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.enums.RoleType;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.user.dao.UserRepository;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileAccountViewModel;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileInfoViewModel;
import com.dimitarrradev.workoutScheduler.util.mapping.user.UserFromBindingModelMapper;
import com.dimitarrradev.workoutScheduler.util.mapping.user.UserToViewModelMapper;
import com.dimitarrradev.workoutScheduler.web.binding.UserProfileAccountEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.UserProfileInfoEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.UserProfilePasswordChangeBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.UserRegisterBindingModel;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.dimitarrradev.workoutScheduler.RandomValueGenerator.randomId;
import static com.dimitarrradev.workoutScheduler.RandomValueGenerator.randomWorkoutType;
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
    private UserFromBindingModelMapper mapperFrom;
    @Mock
    private UserToViewModelMapper mapperTo;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                randomId(),
                "existing",
                "first",
                "last",
                "user@email",
                "old-password",
                90,
                181,
                30,
                "Gym",
                randomWorkoutType(),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(new Role(1L, RoleType.USER, Collections.emptyList())),
                Collections.emptyList(),
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

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

        UserRegisterBindingModel bindingModel = new UserRegisterBindingModel(
                "existing",
                "valid@email",
                "valid password",
                "valid password");

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userService.doRegister(bindingModel)
        );
    }

    @Test
    void testDoRegisterThrowsWhenUserWithEmailAlreadyExistsInRepository() {
        when(userRepository.existsUserByEmail("existing@email"))
                .thenReturn(true);

        UserRegisterBindingModel bindingModel = new UserRegisterBindingModel(
                "valid username",
                "existing@email",
                "valid password",
                "valid password");

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.doRegister(bindingModel)
        );
    }

    @Test
    void testDoRegisterThrowsWhenPasswordAndConfirmPasswordAreNotEqual() {
        UserRegisterBindingModel bindingModel = new UserRegisterBindingModel(
                "valid username",
                "valid@email",
                "password",
                "not equal password");

        assertThrows(
                PasswordsDoNotMatchException.class,
                () -> userService.doRegister(bindingModel)
        );
    }

    @Test
    void testDoRegisterCreatesAndSavesNewUserWithCorrectData() {
        UserRegisterBindingModel bindingModel = new UserRegisterBindingModel("valid username", "valid@email", "validPassword", "validPassword");
        String encodedPassword = "" + bindingModel.password().hashCode() * 0.98765432123456789;

        when(userRepository.existsUserByUsername(bindingModel.username()))
                .thenReturn(false);
        when(userRepository.existsUserByEmail(bindingModel.email()))
                .thenReturn(false);

        Role userRole = new Role(1L, RoleType.USER, new ArrayList<>());

        User expected = new User();
        expected.setUsername(bindingModel.username());
        expected.setEmail(bindingModel.email());
        expected.setPassword(encodedPassword);
        expected.setRoles(new ArrayList<>(List.of(userRole)));

        when(mapperFrom.fromUserRegisterBindingModel(bindingModel))
                .thenReturn(expected);

        when(userRepository.save(expected))
                .thenReturn(expected);

        userService.doRegister(bindingModel);

        verify(
                userRepository,
                times(1))
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

        when(mapperTo.toUserProfileAccountViewModel(user))
                .thenReturn(expected);

        assertThat(userService.getUserProfileAccountView(user.getUsername()))
                .isEqualTo(expected);
    }

    @Test
    void testGetUserProfileAccountViewThrowsWhenUserNotFound() {
        when(userRepository.findUserByUsername("not existing"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserProfileAccountView("not existing")
        );
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

        when(mapperTo.toUserProfileInfoViewModel(user))
                .thenReturn(expected);

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

        when(mapperTo.toUserProfileInfoViewModel(user))
                .thenReturn(expected);

        assertThat(userService.getUserProfileInfoView(user.getUsername()))
                .isEqualTo(expected);
    }

    @Test
    void testGetUserProfileInfoViewThrowsWhenUserNotFound() {
        when(userRepository.findUserByUsername("not existing"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserProfileInfoView("not existing")
        );
    }

    @Test
    void testDoPasswordChangeThrowsWhenUserNotFoundInRepository() {
        when(userRepository.findUserByUsername("not existing"))
                .thenReturn(Optional.empty());

        UserProfilePasswordChangeBindingModel bindingModel = new UserProfilePasswordChangeBindingModel(
                "old-password",
                "new-password",
                "new-password"
        );

        assertThrows(
                UserNotFoundException.class,
                () -> userService.doPasswordChange("not existing", bindingModel)
        );
    }

    @Test
    void testDoPasswordChangeThrowsWhenOldPasswordDoesNotMatch() {
        when(userRepository.findUserByUsername("existing"))
                .thenReturn(Optional.of(user));

        UserProfilePasswordChangeBindingModel bindingModel = new UserProfilePasswordChangeBindingModel(
                "not-matching-password",
                "new-password",
                "new-password"
        );

        when(passwordEncoder.matches(bindingModel.oldPassword(), user.getPassword()))
                .thenReturn(Boolean.FALSE);

        assertThrows(
                InvalidPasswordException.class,
                () -> userService.doPasswordChange("existing", bindingModel)
        );
    }

    @Test
    void testDoPasswordChangeThrowsWhenOldPasswordMatchesNewPassword() {
        when(userRepository.findUserByUsername("existing"))
                .thenReturn(Optional.of(user));

        UserProfilePasswordChangeBindingModel bindingModel = new UserProfilePasswordChangeBindingModel(
                "old-password",
                "old-password",
                "old-password"
        );

        when(passwordEncoder.matches(bindingModel.oldPassword(), user.getPassword()))
                .thenReturn(Boolean.TRUE);

        assertThrows(
                InvalidPasswordException.class,
                () -> userService.doPasswordChange("existing", bindingModel)
        );
    }

    @Test
    void testDoPasswordChangeThrowsWhenNewPasswordAndConfirmPasswordDoNotMatch() {
        when(userRepository.findUserByUsername("existing"))
                .thenReturn(Optional.of(user));

        UserProfilePasswordChangeBindingModel bindingModel = new UserProfilePasswordChangeBindingModel(
                "old-password",
                "new-password",
                "not-matching-password"
        );

        when(passwordEncoder.matches(bindingModel.oldPassword(), user.getPassword()))
                .thenReturn(Boolean.TRUE);

        assertThrows(
                InvalidPasswordException.class,
                () -> userService.doPasswordChange("existing", bindingModel)
        );
    }

    @Test
    void testDoPasswordChangeSavesUserWithNewEncodedPasswordInRepository() {
        when(userRepository.findUserByUsername("existing"))
                .thenReturn(Optional.of(user));

        UserProfilePasswordChangeBindingModel bindingModel = new UserProfilePasswordChangeBindingModel(
                "old-password",
                "new-password",
                "new-password"
        );

        when(passwordEncoder.matches(bindingModel.oldPassword(), user.getPassword()))
                .thenReturn(Boolean.TRUE);

        String encodedPassword = "Encoded" + bindingModel.newPassword() + bindingModel.newPassword().hashCode() / 0.88888;

        when(passwordEncoder.encode(bindingModel.newPassword()))
                .thenReturn(encodedPassword);

        userService.doPasswordChange("existing", bindingModel);

        assertThat(user.getPassword())
                .isEqualTo(encodedPassword);
        verify(userRepository,
                times(1))
                .save(user);
    }

    @Test
    void testDoInfoEditThrowsWhenUserNotFoundInRepository() {
        when(userRepository.findUserByUsername("not-existing"))
                .thenReturn(Optional.empty());

        UserProfileInfoEditBindingModel bindingModel = new UserProfileInfoEditBindingModel(
                90,
                181,
                "gym",
                WorkoutType.CARDIO
        );

        assertThrows(
                UserNotFoundException.class,
                () -> userService.doInfoEdit("not-existing", bindingModel)
        );
    }

    @Test
    void testDoInfoEditSavesUserWithNewProfileInformationInRepository() {
        when(userRepository.findUserByUsername("existing"))
                .thenReturn(Optional.of(user));

        UserProfileInfoEditBindingModel bindingModel = new UserProfileInfoEditBindingModel(
                user.getWeight() + 1,
                user.getHeight() - 1,
                "new-gym",
                WorkoutType.WEIGHTLIFTING
        );

        userService.doInfoEdit("existing", bindingModel);

        assertThat(user.getWeight())
                .isEqualTo(bindingModel.weight());
        assertThat(user.getHeight())
                .isEqualTo(bindingModel.height());
        assertThat(user.getGym())
                .isEqualTo(bindingModel.gym());
        assertThat(user.getWorkoutType())
                .isEqualTo(bindingModel.workoutType());

        verify(
                userRepository,
                times(1))
                .save(user);
    }

    @Test
    void testDoAccountEditThrowsWhenUserNotFoundInRepository() {
        when(userRepository.findUserByUsername("not-existing"))
                .thenReturn(Optional.empty());


        UserProfileAccountEditBindingModel bindingModel = new UserProfileAccountEditBindingModel(
                "not-existing",
                user.getEmail(),
                "new" + user.getFirstName(),
                "new" + user.getLastName(),
                Boolean.FALSE);

        assertThrows(
                UserNotFoundException.class,
                () -> userService.doAccountEdit(bindingModel)
        );
    }

    @Test
    void testDoAccountEditSavesUserWithNewAccountInformationInRepository() {
        when(userRepository.findUserByUsername("existing"))
                .thenReturn(Optional.of(user));


        UserProfileAccountEditBindingModel bindingModel = new UserProfileAccountEditBindingModel(
                "existing",
                user.getEmail(),
                "new" + user.getFirstName(),
                "new" + user.getLastName(),
                Boolean.FALSE);

        userService.doAccountEdit(bindingModel);

        assertThat(user.getFirstName())
                .isEqualTo(bindingModel.firstName());
        assertThat(user.getLastName())
                .isEqualTo(bindingModel.lastName());

        verify(
                userRepository,
                times(1))
                .save(user);
    }

    @Test
    void testGetUserEntityByUsernameThrowsWhenUserNotFoundInRepository() {
        when(userRepository.findUserByUsername("not-existing"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserEntityByUsername("not-existing")
        );
    }

    @Test
    void testGetUserEntityByUsernameReturnsCorrectUserEntity() {
        when(userRepository.findUserByUsername("not-existing"))
                .thenReturn(Optional.of(user));

        assertThat(userService.getUserEntityByUsername("not-existing"))
                .isEqualTo(user);
    }

}
