package com.dimitarrradev.workoutScheduler.role.dao;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.enums.RoleType;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Resource
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> getRolesBy();

    Optional<Role> getRoleByRoleType(RoleType roleType);

    boolean existsRoleByRoleType(RoleType roleType);
}
