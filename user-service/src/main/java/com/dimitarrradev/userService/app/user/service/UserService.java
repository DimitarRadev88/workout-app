package com.dimitarrradev.userService.app.user.service;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.controller.binding.UserEditModel;
import com.dimitarrradev.userService.app.controller.binding.UserPasswordChangeModel;
import com.dimitarrradev.userService.app.error.exception.EmailAlreadyExistsException;
import com.dimitarrradev.userService.app.error.exception.InvalidResetTokenException;
import com.dimitarrradev.userService.app.error.exception.UserNotFoundException;
import com.dimitarrradev.userService.app.error.exception.UsernameAlreadyExistsException;
import com.dimitarrradev.userService.app.role.Role;
import com.dimitarrradev.userService.app.role.dao.RoleRepository;
import com.dimitarrradev.userService.app.role.enums.RoleType;
import com.dimitarrradev.userService.app.user.PasswordResetToken;
import com.dimitarrradev.userService.app.user.User;
import com.dimitarrradev.userService.app.user.UserModel;
import com.dimitarrradev.userService.app.user.dao.PasswordResetTokenRepository;
import com.dimitarrradev.userService.app.user.dao.UserRepository;
import com.dimitarrradev.userService.app.user.util.FromModelMapper;
import com.dimitarrradev.userService.app.user.util.UserModelAssembler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserModelAssembler assembler;
    private final FromModelMapper fromModelMapper;
    private final RoleRepository roleRepository;

    @Transactional
    public UserModel createUser(UserAddModel userAddModel) {
        if (this.userRepository.existsUserByUsername(userAddModel.username())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (this.userRepository.existsUserByEmail(userAddModel.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = this.fromModelMapper.fromUserAddModel(userAddModel);

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setRoles(new ArrayList<>(List.of(roleRepository.findRoleByRoleType(RoleType.USER)
                .orElse(new Role(null, RoleType.USER, List.of(user))))));
        user.setBmi(calculateBMI(user));

        return this.assembler.toModel(this.userRepository.save(user));
    }

    public UserModel getUser(Long id) {
        User user = this.userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        return this.assembler.toModel(user);
    }

    public UserModel updateUser(Long id, UserEditModel userEditModel) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        User editedUser = fromModelMapper.fromUserEditModel(user, userEditModel);
        editedUser.setBmi(calculateBMI(editedUser));

        User updatedUser = this.userRepository.save(editedUser);

        return this.assembler.toModel(updatedUser);
    }

    public void createPasswordResetToken(String email) {
        User user = this.userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        PasswordResetToken token = new PasswordResetToken(null, UUID.randomUUID().toString(), user);
        this.passwordResetTokenRepository.save(token);
    }

    @Transactional
    public UserModel resetPassword(UserPasswordChangeModel passwordChangeModel) {
        PasswordResetToken token = this.passwordResetTokenRepository
                .findByTokenAndUser_emailAndExpirationDateIsAfter(passwordChangeModel.token(), passwordChangeModel.email(), LocalDateTime.now())
                .orElseThrow(() -> new InvalidResetTokenException("Invalid reset token!"));

        User user = token.getUser();

        user.setPassword(this.passwordEncoder.encode(passwordChangeModel.password()));

        User saved = this.userRepository.save(user);

        this.passwordResetTokenRepository.delete(token);

        return this.assembler.toModel(saved);
    }

    private static Double calculateBMI(User user) {
        return user.getWeight() != null && user.getHeight() != null ? user.getWeight() / Math.pow(user.getHeight(), 2) : null;
    }

}
