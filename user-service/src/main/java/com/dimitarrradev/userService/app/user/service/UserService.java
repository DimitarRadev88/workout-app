package com.dimitarrradev.userService.app.user.service;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.error.exception.EmailAlreadyExistsException;
import com.dimitarrradev.userService.app.error.exception.UsernameAlreadyExistsException;
import com.dimitarrradev.userService.app.role.enums.RoleType;
import com.dimitarrradev.userService.app.role.service.RoleService;
import com.dimitarrradev.userService.app.user.User;
import com.dimitarrradev.userService.app.user.dao.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public long getUserCount() {
        return userRepository.count();
    }

    @Transactional
    public User createUser(UserAddModel userAddModel) {
        if (userRepository.existsUserByUsername(userAddModel.username())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsUserByEmail(userAddModel.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User(null,
                userAddModel.username(),
                userAddModel.firstName(),
                userAddModel.lastName(),
                userAddModel.email(),
                passwordEncoder.encode(userAddModel.password()),
                userAddModel.weight(),
                userAddModel.height(),
                userAddModel.weight() != null && userAddModel.height() != null ? userAddModel.height() / Math.pow(userAddModel.weight(), 2) : null,
                userAddModel.gym(),
                new ArrayList<>(List.of(roleService.getRoleByType(RoleType.USER))),
                null,
                null
                );

        return userRepository.save(user);
    }


    public User getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        user.setRoles(null);
        return user;
    }
}
