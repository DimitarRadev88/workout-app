package com.dimitarrradev.workoutScheduler;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.RoleService;
import com.dimitarrradev.workoutScheduler.role.RoleType;
import com.dimitarrradev.workoutScheduler.training.Exercise;
import com.dimitarrradev.workoutScheduler.training.Program;
import com.dimitarrradev.workoutScheduler.training.Workout;
import com.dimitarrradev.workoutScheduler.training.dao.ProgramDao;
import com.dimitarrradev.workoutScheduler.training.dao.WorkoutDao;
import com.dimitarrradev.workoutScheduler.user.UserDao;
import com.dimitarrradev.workoutScheduler.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ConsoleRunner implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;

    public ConsoleRunner(UserService userService, RoleService roleService, ProgramDao programDao, WorkoutDao workoutDao) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    @Transactional
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
