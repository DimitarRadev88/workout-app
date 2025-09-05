package com.dimitarrradev.workoutScheduler.role.dao;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.enums.RoleType;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Resource
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> getRolesBy();

    Role getRolesByRoleType(RoleType roleType);
}
