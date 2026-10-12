package com.dimitarrradev.workoutScheduler.util.mapping.user;

import com.dimitarrradev.workoutScheduler.role.Role;
import com.dimitarrradev.workoutScheduler.role.dao.RoleRepository;
import com.dimitarrradev.workoutScheduler.role.enums.RoleType;
import com.dimitarrradev.workoutScheduler.role.service.RoleService;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.web.binding.UserRegisterBindingModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserFromBindingModelMapperUnitTests {

    @InjectMocks
    private UserFromBindingModelMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;

    @Test
    void testFromUserRegisterBindingModelReturnsUserWithCorrectData() {
        UserRegisterBindingModel bindingModel = new UserRegisterBindingModel(
                "test-user",
                "test-user@email",
                "testuserstrongpassword",
                "testuserstrongpassword"

        );

        when(passwordEncoder.encode(bindingModel.password()))
                .thenReturn(bindingModel.password() + "encoded");
        when(roleService.getRoleByType(RoleType.USER))
                .thenReturn(new Role(1L, RoleType.ADMIN, null));

        User expected = new User();
        expected.setUsername(bindingModel.username());
        expected.setPassword(bindingModel.password() + "encoded");
        expected.setEmail(bindingModel.email());
        expected.setRoles(new ArrayList<>(List.of(new Role(1L, RoleType.ADMIN, null))));

        User actual = mapper.fromUserRegisterBindingModel(bindingModel);

        assertThat(actual)
                .isEqualTo(expected);

    }

}
