package com.dimitarrradev.userService.app.user;

import com.dimitarrradev.userService.app.role.Role;
import com.dimitarrradev.userService.app.role.dao.RoleRepository;
import com.dimitarrradev.userService.app.role.enums.RoleType;
import com.dimitarrradev.userService.app.user.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ConsoleRunnerTest implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        roleRepository.save(new Role(null, RoleType.USER, Collections.emptyList()));
        userRepository.save(new User(null, "username", null, null, "some@email.com", "password", null, null, null, null, roleRepository.findAll(), null, null));
    }
}
