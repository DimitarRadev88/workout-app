package com.dimitarrradev.demo.role;

import org.springframework.stereotype.Service;

import java.util.Collection;
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
}
