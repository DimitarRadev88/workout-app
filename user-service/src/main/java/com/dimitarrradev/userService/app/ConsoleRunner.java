package com.dimitarrradev.userService.app;

import com.dimitarrradev.userService.app.role.dao.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsoleRunner implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
//        if (roleRepository.count() == 0) {
//            Role admin = new Role(null, RoleType.ADMIN, new ArrayList<>());
//            Role user = new Role(null, RoleType.USER, new ArrayList<>());
//
//            roleRepository.save(user);
//            roleRepository.save(admin);
//        }
    }
}
