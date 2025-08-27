package com.dimitarrradev.workoutScheduler.role.service;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.dao.RoleDao;
import com.dimitarrradev.workoutScheduler.role.enums.RoleType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleDao roleDao;

    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public List<Role> getRoles() {
        return roleDao.getRolesBy();
    }

    public void addRole(Role role) {
        roleDao.save(role);
    }

    public Role getRoleByType(RoleType roleType) {
        return roleDao.getRolesByRoleType(roleType);
    }
}
