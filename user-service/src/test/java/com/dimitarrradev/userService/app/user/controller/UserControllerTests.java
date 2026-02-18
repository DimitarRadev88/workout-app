package com.dimitarrradev.userService.app.user.controller;

import com.dimitarrradev.userService.app.role.dao.RoleRepository;
import com.dimitarrradev.userService.app.user.User;
import com.dimitarrradev.userService.app.user.dao.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    private User user;


    @Test
    void test() throws Exception {
        this.mockMvc.perform(get("http://localhost:8093/api/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void test1() throws Exception {
        this.mockMvc.perform(get("http://localhost:8093/api/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"));
    }


}
