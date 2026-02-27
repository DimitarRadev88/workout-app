package com.dimitarrradev.userService.app.role.service;

import com.dimitarrradev.userService.app.role.Role;
import com.dimitarrradev.userService.app.role.RoleModel;
import com.dimitarrradev.userService.app.role.dao.RoleRepository;
import com.dimitarrradev.userService.app.role.enums.RoleType;
import com.dimitarrradev.userService.app.role.util.RoleModelAssembler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RoleServiceTests {

    @InjectMocks
    private RoleService roleService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RoleModelAssembler assembler;

    @Test
    void testGetRoleByTypeReturnsCorrectModel() {
        RoleModel model = new RoleModel(1L, RoleType.USER);

        Role role = new Role(1L, RoleType.USER, Collections.emptyList());
        when(this.roleRepository.findRoleByRoleType(RoleType.USER))
                .thenReturn(Optional.of(role));

        when(assembler.toModel(role))
                .thenReturn(model);

        assertEquals(model, roleService.getRoleByType(RoleType.USER));

    }

    @Test
    void testGetUserRolesReturnsCorrectCollectionModelOfRoles() {
        RoleModel userModel = new RoleModel(1L, RoleType.USER);
        RoleModel adminModel = new RoleModel(2L, RoleType.ADMIN);

        Role user = new Role(1L, RoleType.USER, Collections.emptyList());
        Role admin = new Role(2L, RoleType.ADMIN, Collections.emptyList());

        ArrayList<Role> roles = new ArrayList<>(List.of(user, admin));

        when(this.roleRepository.findAllByAndUsers_id(anyLong()))
                .thenReturn(roles);

        CollectionModel<RoleModel> collectionModel = CollectionModel.of(List.of(userModel, adminModel));

        when(assembler.toCollectionModel(roles))
                .thenReturn(collectionModel);


        assertEquals(collectionModel, roleService.getUserRoles(1L));

    }


}
