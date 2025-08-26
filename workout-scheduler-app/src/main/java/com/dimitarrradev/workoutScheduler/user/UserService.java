package com.dimitarrradev.workoutScheduler.user;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.RoleService;
import com.dimitarrradev.workoutScheduler.role.RoleType;
import com.dimitarrradev.workoutScheduler.training.TrainingStyle;
import com.dimitarrradev.workoutScheduler.user.dto.UserProfileViewModel;
import com.dimitarrradev.workoutScheduler.web.binding.UserRegisterBindingModel;
import jakarta.transaction.Transactional;
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

    public UserProfileViewModel getUserProfileView(String name) {
        User user = userDao.findUserByUsername(name);
        UserProfileViewModel result = new UserProfileViewModel(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getWeight(),
                user.getHeight(),
                user.getGym(),
                user.getTrainingStyle() != null ? user.getTrainingStyle() : TrainingStyle.CARDIO
        );
        return result;
    }
}
