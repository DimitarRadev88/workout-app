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
import com.dimitarrradev.userService.app.user.util.FromModelMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserModelAssembler assembler;
    private final FromModelMapper fromModelMapper;
    private final RoleService roleService;

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
        user.getRoles().add(this.roleService.getRoleByType(RoleType.USER));
        user.setBmi(getBmi(user));

        return this.assembler.toModel(this.userRepository.save(user));
    }

    public UserModel getUser(Long id) {
        User user = this.userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with that id does not exist!"));

        return this.assembler.toModel(user);
    }

    public UserModel updateUser(Long id, UserEditModel userEditModel) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User does not exist"));

        User editedUser = fromModelMapper.fromUserEditModel(user, userEditModel);
        editedUser.setBmi(getBmi(editedUser));

        User updatedUser = this.userRepository.save(editedUser);

        return this.assembler.toModel(updatedUser);
    }

    private static Double getBmi(User user) {
        return user.getWeight() != null && user.getHeight() != null ? user.getHeight() / Math.pow(user.getWeight(), 2) : null;
    }

}
