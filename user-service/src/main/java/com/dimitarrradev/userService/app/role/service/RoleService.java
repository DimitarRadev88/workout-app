package com.dimitarrradev.userService.app.role.service;

import com.dimitarrradev.userService.app.role.RoleModel;
import com.dimitarrradev.userService.app.role.util.RoleModelAssembler;
import com.dimitarrradev.userService.app.role.dao.RoleRepository;
import com.dimitarrradev.userService.app.role.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleModelAssembler assembler;

    public RoleModel getRoleByType(RoleType roleType) {
        return this.roleRepository.findRoleByRoleType(roleType).map(this.assembler::toModel).
                orElseThrow(() -> new IllegalArgumentException("Role type not found"));
    }

    public CollectionModel<RoleModel> getUserRoles(Long id) {
        return this.assembler.toCollectionModel(this.roleRepository.findAllByAndUsers_id(id));
    }

}
