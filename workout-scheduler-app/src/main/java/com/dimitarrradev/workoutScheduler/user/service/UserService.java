package com.dimitarrradev.workoutScheduler.user.service;

import com.dimitarrradev.workoutScheduler.errors.exception.*;
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
import lombok.RequiredArgsConstructor;
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
        return mapperTo.toUserProfileAccountViewModel(getUserEntityByUsername(username));
    }

    public UserProfileInfoViewModel getUserProfileInfoView(String username) {
        return mapperTo.toUserProfileInfoViewModel(getUserEntityByUsername(username));
    }

    public void doPasswordChange(String username, UserProfilePasswordChangeBindingModel profilePasswordChange) {
        User user = getUserEntityByUsername(username);

        if (!passwordEncoder.matches(profilePasswordChange.oldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Incorrect password");
        }

        if (profilePasswordChange.newPassword().equals(profilePasswordChange.oldPassword())) {
            throw new InvalidPasswordException("New password cannot be the same as the old");
        }

        if (!profilePasswordChange.newPassword().equals(profilePasswordChange.confirmPassword())) {
            throw new InvalidPasswordException("Confirm password does not match");
        }

        user.setPassword(passwordEncoder.encode(profilePasswordChange.newPassword()));

        userRepository.save(user);
    }

    public void doInfoEdit(String username, UserProfileInfoEditBindingModel profileInfoEdit) {
        User user = getUserEntityByUsername(username);
        user.setWeight(profileInfoEdit.weight());
        user.setHeight(profileInfoEdit.height());
        user.setGym(profileInfoEdit.gym());
        user.setWorkoutType(profileInfoEdit.workoutType());

        userRepository.save(user);
    }

    public void doAccountEdit(UserProfileAccountEditBindingModel profileAccountEdit) {
        User user = getUserEntityByUsername(profileAccountEdit.username());
        user.setFirstName(profileAccountEdit.firstName());
        user.setLastName(profileAccountEdit.lastName());

        userRepository.save(user);
    }

    public User getUserEntityByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("You must login to access the wanted resources"));
    }

}
