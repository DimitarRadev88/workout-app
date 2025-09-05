package com.dimitarrradev.workoutScheduler.role.service;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.dao.RoleRepository;
import com.dimitarrradev.workoutScheduler.role.enums.RoleType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getRoles() {
        return roleRepository.getRolesBy();
    }

    public void addRole(Role role) {
        roleRepository.save(role);
    }

    public Role getRoleByType(RoleType roleType) {
        return roleRepository.getRolesByRoleType(roleType);
    }
}
