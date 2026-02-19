package com.dimitarrradev.userService.app.user.controller;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.role.dao.RoleRepository;
import com.dimitarrradev.userService.app.role.service.RoleService;
import com.dimitarrradev.userService.app.user.User;
import com.dimitarrradev.userService.app.user.dao.UserRepository;
import com.dimitarrradev.userService.app.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTests {

    private static MockHttpServletRequest request;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private UserService userServiceMock;

    @Value("${sql.script.create.user}")
    private String createUser;
    @Value("${sql.script.delete.user}")
    private String deleteUser;
    @Value("${sql.script.create.role}")
    private String createRole;
    @Value("${sql.script.delete.role}")
    private String deleteRole;
    @Value("${sql.script.create.users_roles}")
    private String createUsersRoles;
    @Value("${sql.script.delete.users_roles}")
    private String deleteUsersRoles;

    @BeforeAll
    static void setRequest() {
        request = new MockHttpServletRequest();
    }

    @BeforeEach
    void setUpDatabase() {
        jdbc.execute(createUser);
        jdbc.execute(createRole);
        jdbc.execute(createUsersRoles);
    }

    @Test
    void testGetUserResponseWithExistingUser() throws Exception {
        User user = User.builder().username("test-user-1").email("test_user_1@email.com").password("ValidPassword@123!").roles(roleRepository.getRolesBy()).build();

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> byEmail = userRepository.findByEmail(user.getEmail());

        assertTrue(byEmail.isPresent());

        User savedUser = byEmail.get();

        mockMvc.perform(get("https://localhost:8083/api/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id", is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(savedUser.getEmail())))
                .andExpect(jsonPath("$._links.self.href", is(String.format("https://localhost:8083/api/users/%d", savedUser.getId()))))
                .andExpect(jsonPath("$._links.update.href", is(String.format("https://localhost:8083/api/users/%d", savedUser.getId()))))
                .andExpect(jsonPath("$._links.roles.href", is(String.format("https://localhost:8083/api/users/%d/roles", savedUser.getId()))));
    }

    @Test
    void testGetUserResponseWithNotExistingUser() throws Exception {
        Optional<User> byEmail = userRepository.findByEmail("test_user_1@email.com");

        assertFalse(byEmail.isPresent());

        mockMvc.perform(get("/api/users/{id}", 0))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("UserNotFoundException")))
                .andExpect(jsonPath("$.message", is("User does not exist!")));
    }

    @Test
    void testCreateUserCreatesNewUserWithValidUserAddModel() throws Exception {
        UserAddModel userAddModel = new UserAddModel(
                "test-user-1",
                null,
                null,
                "test_user_1@email.com",
                "ValidPassword@123!",
                null,
                null,
                null
        );

        assertFalse(userRepository.existsUserByUsername(userAddModel.username()));

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAddModel))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/hal+json"));

        assertTrue(userRepository.existsUserByUsername(userAddModel.username()));
    }

    @Test
    void testCreateUserReturnsClientErrorWhenEmailIsInvalid() throws Exception {
        UserAddModel userAddModel = new UserAddModel(
                "test-user-1",
                null,
                null,
                "test_user_1@email",
                "ValidPassword@123!",
                null,
                null,
                null
        );

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAddModel))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("InvalidRequestBodyException")))
                .andExpect(jsonPath("$.message", is("Invalid request body fields!")))
                .andExpect(jsonPath("$.fields", hasSize(1)));

        assertFalse(userRepository.existsUserByUsername(userAddModel.username()));
    }

    @Test
    void testCreateUserReturnsClientErrorWhenPasswordIsInvalid() throws Exception {
        UserAddModel userAddModel = new UserAddModel(
                "test-user-1",
                null,
                null,
                "test_user_1@email.com",
                "invalidpassword",
                null,
                null,
                null
        );

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAddModel))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("InvalidRequestBodyException")))
                .andExpect(jsonPath("$.message", is("Invalid request body fields!")))
                .andExpect(jsonPath("$.fields", hasSize(1)));

        assertFalse(userRepository.existsUserByUsername(userAddModel.username()));
    }

    @Test
    void testGetRolesReturnsCorrectResponseWhenUserExists() throws Exception {
        Optional<User> byId = userRepository.findById(1L);

        assertTrue(byId.isPresent());

        User user = byId.get();

        mockMvc.perform(get("/api/users/{id}/roles", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.roleModelList", hasSize(user.getRoles().size())))
                .andExpect(jsonPath("$._embedded.roleModelList.[0]", hasKey("roleType")))
                .andExpect(jsonPath("$._embedded.roleModelList.[0]", hasValue(user.getRoles().getFirst().getRoleType().name())));
    }

    @AfterEach
    void cleanUpDatabase() {
        jdbc.execute(deleteUsersRoles);
        jdbc.execute(deleteUser);
        jdbc.execute(deleteRole);
    }


}
