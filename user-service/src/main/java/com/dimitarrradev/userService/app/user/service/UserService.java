package com.dimitarrradev.userService.app.user.service;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.controller.binding.UserEditModel;
import com.dimitarrradev.userService.app.error.exception.EmailAlreadyExistsException;
import com.dimitarrradev.userService.app.error.exception.UserNotFoundException;
import com.dimitarrradev.userService.app.error.exception.UsernameAlreadyExistsException;
import com.dimitarrradev.userService.app.role.enums.RoleType;
import com.dimitarrradev.userService.app.role.service.RoleService;
import com.dimitarrradev.userService.app.user.User;
import com.dimitarrradev.userService.app.user.UserModel;
import com.dimitarrradev.userService.app.user.UserModelAssembler;
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
    private final UserModelAssembler assembler;
    private final RoleService roleService;

    @Transactional
    public UserModel createUser(UserAddModel userAddModel) {
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

        return assembler.toModel(userRepository.save(user));
    }

    public UserModel getUser(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with that id does not exist!"));

        return assembler.toModel(user);
    }

    public void updateUser(Long id, UserEditModel userEditModel) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User does not exist"));

        user.setFirstName(userEditModel.firstName());
        user.setLastName(userEditModel.lastName());
        user.setHeight(userEditModel.height());
        user.setWeight(userEditModel.weight());
        user.setFirstName(userEditModel.firstName());

    }
}
