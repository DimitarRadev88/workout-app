package com.dimitarrradev.demo.user;

import com.dimitarrradev.demo.role.Role;
import com.dimitarrradev.demo.role.RoleService;
import com.dimitarrradev.demo.role.RoleType;
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
    public void addUser(String username, String password) {
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
}
