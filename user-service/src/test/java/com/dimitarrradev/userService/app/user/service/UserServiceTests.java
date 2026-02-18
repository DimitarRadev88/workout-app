package com.dimitarrradev.userService.app.user.service;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.controller.binding.UserEditModel;
import com.dimitarrradev.userService.app.controller.binding.UserPasswordChangeModel;
import com.dimitarrradev.userService.app.error.exception.EmailAlreadyExistsException;
import com.dimitarrradev.userService.app.error.exception.InvalidResetTokenException;
import com.dimitarrradev.userService.app.error.exception.UserNotFoundException;
import com.dimitarrradev.userService.app.error.exception.UsernameAlreadyExistsException;
import com.dimitarrradev.userService.app.role.Role;
import com.dimitarrradev.userService.app.role.enums.RoleType;
import com.dimitarrradev.userService.app.role.service.RoleService;
import com.dimitarrradev.userService.app.user.PasswordResetToken;
import com.dimitarrradev.userService.app.user.User;
import com.dimitarrradev.userService.app.user.UserModel;
import com.dimitarrradev.userService.app.user.dao.PasswordResetTokenRepository;
import com.dimitarrradev.userService.app.user.dao.UserRepository;
import com.dimitarrradev.userService.app.user.util.FromModelMapper;
import com.dimitarrradev.userService.app.user.util.UserModelAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserModelAssembler assembler;
    @Mock
    private FromModelMapper fromModelMapper;
    @Mock
    private RoleService roleService;

    private UserAddModel userAdd;
    private UserEditModel userEdit;
    private User notSavedUser;
    private User savedUser;
    private UserModel userModel;

    @BeforeEach
    void setup() {
        this.userAdd = new UserAddModel(
                "username",
                "first",
                "last",
                "some@email.com",
                "strongpassword",
                100.0,
                180.0,
                "some gym"
        );

        this.userEdit = new UserEditModel(
                "new first name",
                "new last name",
                101.1,
                179.9,
                "new gym"
        );

        this.notSavedUser = new User(
                null,
                this.userAdd.username(),
                this.userAdd.firstName(),
                this.userAdd.lastName(),
                this.userAdd.email(),
                this.passwordEncoder.encode(userAdd.password()),
                this.userAdd.weight(),
                this.userAdd.height(),
                null,
                this.userAdd.gym(),
                new ArrayList<>(),
                null,
                null
        );

        this.savedUser = new User(
                1L,
                this.notSavedUser.getUsername(),
                this.notSavedUser.getFirstName(),
                this.notSavedUser.getLastName(),
                this.notSavedUser.getEmail(),
                this.notSavedUser.getPassword(),
                this.notSavedUser.getWeight(),
                this.notSavedUser.getHeight(),
                this.notSavedUser.getBmi(),
                this.notSavedUser.getGym(),
                this.notSavedUser.getRoles(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        this.userModel = new UserModel(
                this.savedUser.getId(),
                this.savedUser.getUsername(),
                this.savedUser.getFirstName(),
                this.savedUser.getLastName(),
                this.savedUser.getEmail(),
                this.savedUser.getWeight(),
                this.savedUser.getHeight(),
                this.savedUser.getBmi(),
                this.savedUser.getGym(),
                this.savedUser.getCreatedAt(),
                this.savedUser.getUpdatedAt()
        );
    }

    @Test
    void testCreateUserThrowsWhenUserWithUsernameExists() {
        when(this.userRepository.existsUserByUsername(this.userAdd.username()))
                .thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class,
                () -> this.userService.createUser(this.userAdd));
    }

    @Test
    void testCreateUserThrowsWhenUserWithEmailExists() {
        when(this.userRepository.existsUserByUsername(this.userAdd.username()))
                .thenReturn(false);

        when(this.userRepository.existsUserByEmail(this.userAdd.email()))
                .thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> this.userService.createUser(this.userAdd));
    }

    @Test
    void testCreateUserSavesNewUserInRepository() {
        when(this.userRepository.existsUserByUsername(this.userAdd.username()))
                .thenReturn(false);

        when(this.userRepository.existsUserByEmail(this.userAdd.email()))
                .thenReturn(false);


        when(this.fromModelMapper.fromUserAddModel(this.userAdd))
                .thenReturn(this.notSavedUser);

        when(this.passwordEncoder.encode(this.notSavedUser.getPassword()))
                .thenReturn("encoded password");

        when(this.roleService.getRoleByType(RoleType.USER))
                .thenReturn(new Role(1L, RoleType.USER, new ArrayList<>()));

        this.userService.createUser(this.userAdd);

        assertEquals(this.notSavedUser.getHeight() / Math.pow(this.notSavedUser.getWeight(), 2), this.notSavedUser.getBmi());

        verify(this.userRepository, times(1)).save(this.notSavedUser);


    }

    @Test
    void testCreateUserReturnsUserModelRepository() {
        when(this.userRepository.existsUserByUsername(this.userAdd.username()))
                .thenReturn(false);

        when(this.userRepository.existsUserByEmail(this.userAdd.email()))
                .thenReturn(false);

        when(this.fromModelMapper.fromUserAddModel(this.userAdd))
                .thenReturn(this.notSavedUser);

        when(this.passwordEncoder.encode(this.notSavedUser.getPassword()))
                .thenReturn("encoded password");

        when(this.roleService.getRoleByType(RoleType.USER))
                .thenReturn(new Role(1L, RoleType.USER, new ArrayList<>()));

        when(this.userRepository.save(this.notSavedUser))
                .thenReturn(this.savedUser);

        UserModel expected = new UserModel(
                this.savedUser.getId(),
                this.savedUser.getUsername(),
                this.savedUser.getFirstName(),
                this.savedUser.getLastName(),
                this.savedUser.getEmail(),
                this.savedUser.getWeight(),
                this.savedUser.getHeight(),
                this.savedUser.getBmi(),
                this.savedUser.getGym(),
                this.savedUser.getCreatedAt(),
                this.savedUser.getUpdatedAt()
        );

        when(this.assembler.toModel(this.savedUser))
                .thenReturn(expected);

        assertEquals(expected, this.userService.createUser(this.userAdd));
    }

    @Test
    void testGetUserThrowsWhenUserDoesNotExist() {
        when(this.userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> this.userService.getUser(1L)
        );
    }

    @Test
    void testGetUserReturnsCorrectUserModel() {
        User user = new User(
                1L,
                this.userAdd.username(),
                this.userAdd.firstName(),
                this.userAdd.lastName(),
                this.userAdd.email(),
                this.passwordEncoder.encode(userAdd.password()),
                this.userAdd.weight(),
                this.userAdd.height(),
                this.userAdd.height() / Math.pow(this.userAdd.weight(), 2),
                this.userAdd.gym(),
                new ArrayList<>(List.of(new Role(1L, RoleType.USER, Collections.emptyList()))),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(this.userRepository.findById(1L))
                .thenReturn(Optional.of(user));


        when(this.assembler.toModel(user))
                .thenReturn(this.userModel);

        assertEquals(this.userModel, this.userService.getUser(user.getId()));
    }

    @Test
    void testUpdateUserThrowsWhenUserDoesNotExist() {
        when(this.userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> this.userService.updateUser(1L, new UserEditModel(null, null, null, null, null))
        );
    }

    @Test
    void testUpdateUserSavesUserWithNewData() {
        when(this.userRepository.findById(this.savedUser.getId()))
                .thenReturn(Optional.of(this.savedUser));

        User mappedUser = new User(
                this.savedUser.getId(),
                this.savedUser.getUsername(),
                this.userEdit.firstName(),
                this.userEdit.lastName(),
                this.savedUser.getEmail(),
                this.savedUser.getPassword(),
                this.userEdit.weight(),
                this.userEdit.height(),
                null,
                this.userEdit.gym(),
                this.savedUser.getRoles(),
                this.savedUser.getCreatedAt(),
                null
        );

        when(fromModelMapper.fromUserEditModel(this.savedUser, this.userEdit))
                .thenReturn(mappedUser);

        this.userService.updateUser(this.savedUser.getId(), this.userEdit);

        assertEquals(this.userEdit.firstName(), mappedUser.getFirstName());
        assertEquals(this.userEdit.lastName(), mappedUser.getLastName());
        assertEquals(this.userEdit.height(), mappedUser.getHeight());
        assertEquals(this.userEdit.weight(), mappedUser.getWeight());
        assertEquals(this.userEdit.gym(), mappedUser.getGym());

        verify(this.userRepository, times(1)).save(mappedUser);
    }

    @Test
    void testCreatePasswordResetTokenThrowsWhenUserNotExisting() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.createPasswordResetToken("missing@email.com"));
    }

    @Test
    void testCreatePasswordResetTokenCreatesNewTokenAndSavesItInRepository() {
        when(userRepository.findByEmail(savedUser.getEmail()))
                .thenReturn(Optional.of(savedUser));

        userService.createPasswordResetToken(savedUser.getEmail());

        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    void testResetPasswordThrowsWhenInvokedWithInvalidDataModelFields() {
        when(passwordResetTokenRepository
                .findByTokenAndUser_emailAndExpirationDateIsAfter(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(InvalidResetTokenException.class, () -> userService
                .resetPassword(new UserPasswordChangeModel("invalid@email.com", "new password", UUID.randomUUID().toString())));
    }

    @Test
    void testResetPasswordSavesNewPasswordWhenDataModelFieldsAreValid() {
        PasswordResetToken resetToken = new PasswordResetToken(
                1L,
                UUID.randomUUID().toString(),
                savedUser
        );

        UserPasswordChangeModel passwordChangeModel = new UserPasswordChangeModel(
                savedUser.getEmail(),
                "@ValidPassword123",
                resetToken.getToken()
        );

        when(passwordResetTokenRepository
                .findByTokenAndUser_emailAndExpirationDateIsAfter(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(resetToken));

        when(passwordEncoder.encode(passwordChangeModel.password())).thenReturn("encoded" + passwordChangeModel.password());

        userService.resetPassword(passwordChangeModel);

        assertEquals("encoded" + passwordChangeModel.password(), savedUser.getPassword());
        verify(userRepository, times(1)).save(savedUser);
        verify(passwordResetTokenRepository, times(1)).delete(resetToken);
    }

}
