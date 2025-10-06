package com.dimitarrradev.workoutScheduler.user.service;

import com.dimitarrradev.workoutScheduler.errors.exception.EmailAlreadyExistsException;
import com.dimitarrradev.workoutScheduler.errors.exception.PasswordsDoNotMatchException;
import com.dimitarrradev.workoutScheduler.errors.exception.UsernameAlreadyExistsException;
import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.service.RoleService;
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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserFromBindingModelMapper mapperFrom;
    private final UserToViewModelMapper mapperTo;

    @Transactional
    public void addFirstUserAsAdmin(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(username + "@email.com");
        List<Role> roles = roleService.getRoles();
        user.setRoles(roles);

        userRepository.save(user);
    }

    public long getUserCount() {
        return userRepository.count();
    }

    @Transactional
    public String doRegister(UserRegisterBindingModel userRegisterBindingModel) {
        if (!userRegisterBindingModel.password().equals(userRegisterBindingModel.confirmPassword())) {
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }

        if (userRepository.existsUserByUsername(userRegisterBindingModel.username())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsUserByEmail(userRegisterBindingModel.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = mapperFrom.fromUserRegisterBindingModel(userRegisterBindingModel);

        User saved = userRepository.save(user);

        return saved.getUsername();
    }

    public UserProfileAccountViewModel getUserProfileAccountView(String username) {
        return userRepository
                .findUserByUsername(username)
                .map(mapperTo::toUserProfileAccountViewModel)
                .orElseThrow(() -> new UsernameNotFoundException(username));

    }

    public UserProfileInfoViewModel getUserProfileInfoView(String username) {
        return userRepository.findUserByUsername(username)
                .map(mapperTo::toUserProfileInfoViewModel)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public void doPasswordChange(String username, @Valid UserProfilePasswordChangeBindingModel profilePasswordChange) {
        User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (!passwordEncoder.matches(profilePasswordChange.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        if (profilePasswordChange.newPassword().equals(profilePasswordChange.oldPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the old");
        }

        user.setPassword(passwordEncoder.encode(profilePasswordChange.newPassword()));
        userRepository.save(user);
    }

    public void doInfoEdit(String username, UserProfileInfoEditBindingModel profileInfoEdit) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        user.setWeight(profileInfoEdit.weight());
        user.setHeight(profileInfoEdit.height());
        user.setGym(profileInfoEdit.gym());
        user.setWorkoutType(profileInfoEdit.workoutType());

        userRepository.save(user);
    }


    public void doAccountEdit(UserProfileAccountEditBindingModel profileAccountEdit) {
        User user = userRepository
                .findUserByUsername(profileAccountEdit.username())
                .orElseThrow(() -> new UsernameNotFoundException(profileAccountEdit.username()));

        user.setFirstName(profileAccountEdit.firstName());
        user.setLastName(profileAccountEdit.lastName());

        userRepository.save(user);
    }

    public User getUserEntityByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
