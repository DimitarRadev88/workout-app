package com.dimitarrradev.workoutScheduler;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.RoleService;
import com.dimitarrradev.workoutScheduler.role.RoleType;
import com.dimitarrradev.workoutScheduler.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConsoleRunner implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;

    public ConsoleRunner(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleService.getRoles().size() <= 0L) {
            Role roleUser = new Role();
            roleUser.setRoleType(RoleType.USER);
            Role roleAdmin = new Role();
            roleAdmin.setRoleType(RoleType.ADMIN);
            roleService.addRole(roleUser);
            roleService.addRole(roleAdmin);
        }

        if (userService.getUserCount() <= 0L) {
            userService.addFirstUser("user", "password");
        }

    }
}
