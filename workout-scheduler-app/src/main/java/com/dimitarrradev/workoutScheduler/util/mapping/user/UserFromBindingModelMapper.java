package com.dimitarrradev.workoutScheduler.util.mapping.user;

import com.dimitarrradev.workoutScheduler.role.enums.RoleType;
import com.dimitarrradev.workoutScheduler.role.service.RoleService;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.web.binding.UserRegisterBindingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserFromBindingModelMapper {

    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public User fromUserRegisterBindingModel(UserRegisterBindingModel userRegisterBindingModel) {
        User user = new User();

        user.setUsername(userRegisterBindingModel.username());
        user.setEmail(userRegisterBindingModel.email());
        user.setPassword(passwordEncoder.encode(userRegisterBindingModel.password()));
        user.setRoles(new ArrayList<>(List.of(roleService.getRoleByType(RoleType.USER))));

        return user;
    }

}
