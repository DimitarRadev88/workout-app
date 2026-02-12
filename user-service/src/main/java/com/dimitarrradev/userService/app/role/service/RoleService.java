package com.dimitarrradev.userService.app.role.service;

import com.dimitarrradev.userService.app.role.Role;
import com.dimitarrradev.userService.app.role.RoleModel;
import com.dimitarrradev.userService.app.role.RoleModelAssembler;
import com.dimitarrradev.userService.app.role.dao.RoleRepository;
import com.dimitarrradev.userService.app.role.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleModelAssembler assembler;

    public List<Role> getRoles() {
        return roleRepository.getRolesBy();
    }

    public void addRole(Role role) {
        if (roleRepository.existsRoleByRoleType(role.getRoleType())) {
            throw new IllegalArgumentException("Role already exists");
        }

        roleRepository.save(role);
    }

    public Role getRoleByType(RoleType roleType) {
        return roleRepository.getRoleByRoleType(roleType).
                orElseThrow(() -> new IllegalArgumentException("Role type not found"));
    }

    public CollectionModel<RoleModel> getUserRoles(Long id) {
        return assembler.toCollectionModel(roleRepository.findAllByAndUsers_id(id));
    }

}
