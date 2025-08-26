package com.dimitarrradev.workoutScheduler.user;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.RoleService;
import com.dimitarrradev.workoutScheduler.role.RoleType;
import com.dimitarrradev.workoutScheduler.training.TrainingStyle;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileAccountViewModel;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileInfoViewModel;
import com.dimitarrradev.workoutScheduler.web.binding.UserProfilePasswordChangeBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.UserProfileAccountEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.UserProfileInfoEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.UserRegisterBindingModel;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserService(UserDao userDao, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Transactional
    public void addFirstUser(String username, String password) {
        if (userDao.existsUserByUsername(username)) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(username + "@email.com");
        Role role = roleService.getRoles().getFirst();
        user.setRoles(new ArrayList<>(List.of(role)));

        userDao.save(user);
    }

    public long getUserCount() {
        return userDao.count();
    }

    @Transactional
    public String doRegister(UserRegisterBindingModel userRegisterBindingModel) {
        if (userDao.existsUserByUsername(userRegisterBindingModel.username())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        if (userDao.existsUserByEmail(userRegisterBindingModel.email())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User();
        user.setUsername(userRegisterBindingModel.username());
        user.setEmail(userRegisterBindingModel.email());
        user.setPassword(passwordEncoder.encode(userRegisterBindingModel.password()));
        user.setRoles(new ArrayList<>(List.of(roleService.getRoleByType(RoleType.USER))));

        User saved = userDao.save(user);

        return saved.getUsername();
    }

    public UserProfileAccountViewModel getUserProfileAccountView(String username) {
        User user = userDao.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        UserProfileAccountViewModel result = new UserProfileAccountViewModel(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
        return result;
    }

    public UserProfileInfoViewModel getUserProfileInfoView(String username) {
        User user = userDao.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        UserProfileInfoViewModel result = new UserProfileInfoViewModel(
                user.getWeight(),
                user.getHeight(),
                user.getGym(),
                user.getTrainingStyle() != null ? user.getTrainingStyle() : TrainingStyle.CARDIO
        );
        return result;
    }

    public void doPasswordChange(String username, @Valid UserProfilePasswordChangeBindingModel profilePasswordChange) {
        User user = userDao.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        if (!passwordEncoder.matches(profilePasswordChange.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        } else if (!passwordEncoder.matches(profilePasswordChange.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password is cannot be the same as the old password");
        } else {
            user.setPassword(passwordEncoder.encode(profilePasswordChange.newPassword()));
        }

        userDao.save(user);
    }

    public void doInfoEdit(String username, UserProfileInfoEditBindingModel profileInfoEdit) {
        User user = userDao.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        user.setWeight(profileInfoEdit.weight());
        user.setHeight(profileInfoEdit.height());
        user.setGym(profileInfoEdit.gym());
        user.setTrainingStyle(profileInfoEdit.trainingStyle());

        userDao.save(user);
    }


    public void doAccountEdit(String username, UserProfileAccountEditBindingModel profileAccountEdit) {
        User user = userDao.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        user.setFirstName(profileAccountEdit.firstName());
        user.setLastName(profileAccountEdit.lastName());

        userDao.save(user);
    }
}
