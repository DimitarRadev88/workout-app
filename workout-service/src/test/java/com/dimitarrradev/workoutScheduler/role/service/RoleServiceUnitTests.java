package com.dimitarrradev.workoutScheduler.role.service;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.dao.RoleRepository;
import com.dimitarrradev.workoutScheduler.role.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceUnitTests {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void testGetRolesReturnsListOfRoles() {
        List<Role> list = new ArrayList<>(List.of(new Role()));

        when(roleRepository.getRolesBy())
                .thenReturn(list);

        List<Role> roles = roleService.getRoles();

        assertThat(roles).isEqualTo(list);
    }

    @Test
    void testAddRoleAddsRoleInRepositoryWhenRoleByTypeDoesNotExistInRepository() {
        Role role = new Role(null, RoleType.ADMIN, Collections.emptyList());

        when(roleRepository.existsRoleByRoleType(RoleType.ADMIN))
                .thenReturn(false);

        roleService.addRole(role);
        verify(roleRepository, times(1))
                .save(role);
    }

    @Test
    void testAddRoleThrowsWhenRoleByTypeAlreadyExistsInRepository() {
        Role role = new Role(null, RoleType.ADMIN, Collections.emptyList());

        when(roleRepository.existsRoleByRoleType(RoleType.ADMIN))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> roleService.addRole(role)
        );
    }

    @Test
    void testGetRoleByTypeReturnsCorrectRoleWhenRoleExistsInRepository() {
        Role role = new Role(null, RoleType.ADMIN, Collections.emptyList());

        when(roleRepository.getRoleByRoleType(RoleType.ADMIN))
                .thenReturn(Optional.of(role));

        assertThat(roleService.getRoleByType(RoleType.ADMIN))
                .isEqualTo(role);
    }

    @Test
    void testGetRoleByTypeThrowsWhenRoleDoesNotExistInRepository() {
        when(roleRepository.getRoleByRoleType(RoleType.ADMIN))
        .thenReturn(Optional.empty());
        assertThrows(
                IllegalArgumentException.class,
                () -> roleService.getRoleByType(RoleType.ADMIN));
    }

}
