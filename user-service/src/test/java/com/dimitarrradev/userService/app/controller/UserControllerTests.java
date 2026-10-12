package com.dimitarrradev.userService.app.controller;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.controller.binding.UserEditModel;
import com.dimitarrradev.userService.app.controller.binding.UserPasswordChangeModel;
import com.dimitarrradev.userService.app.role.dao.RoleRepository;
import com.dimitarrradev.userService.app.role.service.RoleService;
import com.dimitarrradev.userService.app.user.PasswordResetToken;
import com.dimitarrradev.userService.app.user.User;
import com.dimitarrradev.userService.app.user.dao.PasswordResetTokenRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private PasswordResetTokenRepository resetTokenRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserService userServiceMock;

    @Value("${server.port}")
    private String serverPort;
    @Value("${sql.script.create.user}")
    private String createUser;
    @Value("${sql.script.delete.user}")
    private String deleteUser;
    @Value("${sql.script.create.role}")
    private String createRole;
    @Value("${sql.script.delete.role}")
    private String deleteRole;
    @Value("${sql.script.delete.password_reset_token}")
    private String deletePasswordResetToken;
    @Value("${sql.script.create.users_roles}")
    private String createUsersRoles;
    @Value("${sql.script.delete.users_roles}")
    private String deleteUsersRoles;
    private final static String APPLICATION_HAL_PLUS_JSON = "application/hal+json";

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

    @AfterEach
    void cleanUpDatabase() {
        jdbc.execute(deleteUsersRoles);
        jdbc.execute(deletePasswordResetToken);
        jdbc.execute("ALTER TABLE password_reset_tokens ALTER COLUMN ID RESTART WITH 1");
        jdbc.execute(deleteUser);
        jdbc.execute("ALTER TABLE users ALTER COLUMN ID RESTART WITH 1");
        jdbc.execute(deleteRole);
        jdbc.execute("ALTER TABLE roles ALTER COLUMN ID RESTART WITH 1");
    }

    @Test
    void testGetUserResponseWithExistingUser() throws Exception {
        User user = User.builder()
                .username("test-user-1")
                .email("test_user_1@email.com")
                .password("ValidPassword@123!")
                .roles(roleRepository.findAll())
                .build();

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> byId = userRepository.findById(1L);

        assertTrue(byId.isPresent());

        User savedUser = byId.get();

        mockMvc.perform(get("https://localhost:" + serverPort + "/api/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_PLUS_JSON))
                .andExpect(jsonPath("$.id", is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(savedUser.getEmail())))
                .andExpect(jsonPath("$._links.self.href", is(String.format("https://localhost:" + serverPort + "/api/users/%d", savedUser.getId()))))
                .andExpect(jsonPath("$._links.update.href", is(String.format("https://localhost:" + serverPort + "/api/users/%d", savedUser.getId()))))
                .andExpect(jsonPath("$._links.roles.href", is(String.format("https://localhost:" + serverPort + "/api/users/%d/roles", savedUser.getId()))));
    }

    @Test
    void testGetUserResponseWithNotExistingUser() throws Exception {
        Optional<User> byId = userRepository.findById(0L);

        assertFalse(byId.isPresent());

        mockMvc.perform(get("https://localhost:" + serverPort + "/api/users/{id}", 0))
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
        assertFalse(userRepository.existsUserByEmail(userAddModel.email()));

        mockMvc.perform(post("https://localhost:" + serverPort + "/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAddModel))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_HAL_PLUS_JSON));

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

        mockMvc.perform(post("https://localhost:" + serverPort + "/api/users/create")
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

        mockMvc.perform(get("https://localhost:" + serverPort + "/api/users/{id}/roles", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_PLUS_JSON))
                .andExpect(jsonPath("$._embedded.roleModelList", hasSize(user.getRoles().size())))
                .andExpect(jsonPath("$._embedded.roleModelList.[0]", hasKey("roleType")))
                .andExpect(jsonPath("$._embedded.roleModelList.[0]", hasValue(user.getRoles().getFirst().getRoleType().name())));
    }

    @Test
    void testUpdateUserReturnsCorrectResponseWhenUserExistsAndBodyIsValid() throws Exception {
        Optional<User> byId = userRepository.findById(1L);

        assertTrue(byId.isPresent());

        User user = byId.get();

        UserEditModel editModel = new UserEditModel(
                "firstName112",
                "lastName11",
                103.0,
                1.88,
                "gym"
        );

        mockMvc.perform(patch("https://localhost:" + serverPort + "/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editModel)))
                .andExpect(content().contentType(APPLICATION_HAL_PLUS_JSON))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.firstName", is(editModel.firstName())))
                .andExpect(jsonPath("$.lastName", is(editModel.lastName())))
                .andExpect(jsonPath("$.weight", is(editModel.weight())))
                .andExpect(jsonPath("$.height", is(editModel.height())))
                .andExpect(jsonPath("$.bmi", notNullValue()))
                .andExpect(jsonPath("$.gym", is(editModel.gym())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andExpect(jsonPath("$._links.self.href", is(String.format("https://localhost:" + serverPort + "/api/users/%d", user.getId()))))
                .andExpect(jsonPath("$._links.update.href", is(String.format("https://localhost:" + serverPort + "/api/users/%d", user.getId()))))
                .andExpect(jsonPath("$._links.roles.href", is(String.format("https://localhost:" + serverPort + "/api/users/%d/roles", user.getId()))));
    }

    @Test
    void testUpdateUserReturnsClientErrorWithInvalidRequestBody() throws Exception {
        UserEditModel editModel = new UserEditModel(
                "firstName",
                "lastName",
                -1.,
                -1.,
                "gym"
        );

        mockMvc.perform(patch("https://localhost:" + serverPort + "/api/users/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editModel))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("InvalidRequestBodyException")))
                .andExpect(jsonPath("$.message", is("Invalid request body fields!")))
                .andExpect(jsonPath("$.fields", hasSize(2)));
    }

    @Test
    void testCreatePasswordResetTokenCreatesTokenWhenUserExists() throws Exception {
        Optional<User> byId = userRepository.findById(1L);

        assertTrue(byId.isPresent());

        User user = byId.get();

        mockMvc.perform(post("https://localhost:" + serverPort + "/api/users/create-password-reset-token")
                .param("email", user.getEmail()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", user.getEmail()));

        Optional<PasswordResetToken> resetToken = resetTokenRepository.findByUser_email(user.getEmail());

        assertTrue(resetToken.isPresent());
    }

    @Test
    void testUpdateUserPasswordReturnsCorrectResponseWhenUserExistsAndBodyIsValid() throws Exception {
        Optional<User> byId = userRepository.findById(1L);

        assertTrue(byId.isPresent());

        User user = byId.get();

        PasswordResetToken passwordResetToken = new PasswordResetToken(
                null,
                UUID.randomUUID().toString(),
                user
        );

        entityManager.persist(passwordResetToken);
        entityManager.flush();

        UserPasswordChangeModel changeModel = new UserPasswordChangeModel(
                user.getEmail(),
                "1NewValidPassword@321",
                passwordResetToken.getToken()
        );

        mockMvc.perform(patch("https://localhost:" + serverPort + "/api/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeModel)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_PLUS_JSON))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andExpect(jsonPath("$._links.self.href", is(String.format("https://localhost:" + serverPort + "/api/users/%d", user.getId()))))
                .andExpect(jsonPath("$._links.update.href", is(String.format("https://localhost:" + serverPort + "/api/users/%d", user.getId()))))
                .andExpect(jsonPath("$._links.roles.href", is(String.format("https://localhost:" + serverPort + "/api/users/%d/roles", user.getId()))));

        Optional<User> optionalUser = userRepository.findById(user.getId());
        assertTrue(optionalUser.isPresent());

        User updatedUser = optionalUser.get();

        assertTrue(passwordEncoder.matches(changeModel.password(), updatedUser.getPassword()));
    }

    @Test
    void testUpdateUserPasswordReturnsClientErrorWithInvalidRequestBody() throws Exception {
        UserPasswordChangeModel changeModel = new UserPasswordChangeModel(
               "",
                "notvalidpassword",
                ""
        );


        mockMvc.perform(patch("https://localhost:" + serverPort + "/api/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeModel))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("InvalidRequestBodyException")))
                .andExpect(jsonPath("$.message", is("Invalid request body fields!")))
                .andExpect(jsonPath("$.fields", hasSize(3)));
    }





}
