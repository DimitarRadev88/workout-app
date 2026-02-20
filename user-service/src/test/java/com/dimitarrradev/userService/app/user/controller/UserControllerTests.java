package com.dimitarrradev.userService.app.user.controller;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    }

    @Test
    void testGetUserResponseWithExistingUser() throws Exception {
        User user = User.builder()
                .username("test-user-1")
                .email("test_user_1@email.com")
                .password("ValidPassword@123!")
                .roles(roleRepository.getRolesBy())
                .build();

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> byEmail = userRepository.findByEmail(user.getEmail());

        assertTrue(byEmail.isPresent());

        User savedUser = byEmail.get();

        mockMvc.perform(get("https://localhost:8083/api/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_PLUS_JSON))
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
        User user = User.builder()
                .username("test-user-1")
                .email("test_user_1@email.com")
                .password("ValidPassword@123!")
                .roles(roleRepository.getRolesBy())
                .build();

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> byUsername = userRepository.findByUsername(user.getUsername());

        assertTrue(byUsername.isPresent());

        User savedUser = byUsername.get();

        mockMvc.perform(get("/api/users/{id}/roles", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_PLUS_JSON))
                .andExpect(jsonPath("$._embedded.roleModelList", hasSize(savedUser.getRoles().size())))
                .andExpect(jsonPath("$._embedded.roleModelList.[0]", hasKey("roleType")))
                .andExpect(jsonPath("$._embedded.roleModelList.[0]", hasValue(savedUser.getRoles().getFirst().getRoleType().name())));
    }

    @Test
    void testUpdateUserReturnsCorrectResponseWhenUserExistsAndBodyIsValid() throws Exception {
        User user = User.builder().
                username("test-user-1")
                .email("test_user_1@email.com")
                .password("ValidPassword@123!")
                .roles(roleRepository.getRolesBy())
                .build();

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> byUsername = userRepository.findByUsername(user.getUsername());

        assertTrue(byUsername.isPresent());

        User savedUser = byUsername.get();

        UserEditModel editModel = new UserEditModel(
                "firstName",
                "lastName",
                101.0,
                1.81,
                "gym"
        );

        mockMvc.perform(patch("https://localhost:8083/api/users/{id}", savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editModel)))
                .andExpect(content().contentType(APPLICATION_HAL_PLUS_JSON))
                .andExpect(jsonPath("$.id", is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$.firstName", is("firstName")))
                .andExpect(jsonPath("$.lastName", is("lastName")))
                .andExpect(jsonPath("$.weight", is(101.0)))
                .andExpect(jsonPath("$.height", is(1.81)))
                .andExpect(jsonPath("$.bmi", notNullValue()))
                .andExpect(jsonPath("$.gym", is("gym")))
                .andExpect(jsonPath("$.email", is(savedUser.getEmail())))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andExpect(jsonPath("$._links.self.href", is(String.format("https://localhost:8083/api/users/%d", savedUser.getId()))))
                .andExpect(jsonPath("$._links.update.href", is(String.format("https://localhost:8083/api/users/%d", savedUser.getId()))))
                .andExpect(jsonPath("$._links.roles.href", is(String.format("https://localhost:8083/api/users/%d/roles", savedUser.getId()))));
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

        mockMvc.perform(patch("/api/users/{id}", 0)
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
        User user = User.builder().
                username("test-user-1")
                .email("test_user_1@email.com")
                .password("ValidPassword@123!")
                .roles(roleRepository.getRolesBy())
                .build();

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> byUsername = userRepository.findByUsername(user.getUsername());

        assertTrue(byUsername.isPresent());

        User savedUser = byUsername.get();

        mockMvc.perform(post("https://localhost:8083/api/users/create-password-reset-token")
                .param("email", savedUser.getEmail()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", savedUser.getEmail()));

        Optional<PasswordResetToken> resetToken = resetTokenRepository.findByUser_email(savedUser.getEmail());

        assertTrue(resetToken.isPresent());
    }

    @Test
    void testUpdateUserPasswordReturnsCorrectResponseWhenUserExistsAndBodyIsValid() throws Exception {
        User user = User.builder().
                username("test-user-1")
                .email("test_user_1@email.com")
                .password("ValidPassword@123!")
                .roles(roleRepository.getRolesBy())
                .build();

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> byUsername = userRepository.findByUsername(user.getUsername());

        assertTrue(byUsername.isPresent());

        User savedUser = byUsername.get();

        PasswordResetToken passwordResetToken = new PasswordResetToken(
                null,
                UUID.randomUUID().toString(),
                user
        );

        entityManager.persist(passwordResetToken);
        entityManager.flush();

        UserPasswordChangeModel changeModel = new UserPasswordChangeModel(
                savedUser.getEmail(),
                "1NewValidPassword@321",
                passwordResetToken.getToken()
        );

        mockMvc.perform(patch("https://localhost:8083/api/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeModel)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_PLUS_JSON))
                .andExpect(jsonPath("$.id", is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(savedUser.getUsername())))
                .andExpect(jsonPath("$.email", is(savedUser.getEmail())))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andExpect(jsonPath("$._links.self.href", is(String.format("https://localhost:8083/api/users/%d", savedUser.getId()))))
                .andExpect(jsonPath("$._links.update.href", is(String.format("https://localhost:8083/api/users/%d", savedUser.getId()))))
                .andExpect(jsonPath("$._links.roles.href", is(String.format("https://localhost:8083/api/users/%d/roles", savedUser.getId()))));

        User updatedUser = userRepository.findById(savedUser.getId()).get();

        assertTrue(passwordEncoder.matches(changeModel.password(), updatedUser.getPassword()));
    }

    @Test
    void estUpdateUserPasswordReturnsClientErrorWithInvalidRequestBody() throws Exception {
        UserPasswordChangeModel changeModel = new UserPasswordChangeModel(
               "",
                "notvalidpassword",
                ""
        );


        mockMvc.perform(patch("https://localhost:8083/api/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeModel))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("InvalidRequestBodyException")))
                .andExpect(jsonPath("$.message", is("Invalid request body fields!")))
                .andExpect(jsonPath("$.fields", hasSize(3)));
    }


    @AfterEach
    void cleanUpDatabase() {
        jdbc.execute(deleteUsersRoles);
        jdbc.execute(deletePasswordResetToken);
        jdbc.execute(deleteUser);
        jdbc.execute(deleteRole);
    }


}
