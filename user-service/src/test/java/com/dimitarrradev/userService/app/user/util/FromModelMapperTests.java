package com.dimitarrradev.userService.app.user.util;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FromModelMapperTests {

    @InjectMocks
    private FromModelMapper modelMapper;

    private UserAddModel userAddModel;

    @BeforeEach
    void setup() {
        this.userAddModel = new UserAddModel(
                "username",
                "first",
                "last",
                "newuser@email.com",
                "password123",
                100.0,
                180.0,
                "new user's gym"
        );

    }

    @Test
    void testFromUserAddModelReturnsUserWithCorrectFields() {
        User user = new User(null,
                userAddModel.username(),
                userAddModel.firstName(),
                userAddModel.lastName(),
                userAddModel.email(),
                userAddModel.password(),
                userAddModel.weight(),
                userAddModel.height(),
                null,
                userAddModel.gym(),
                new ArrayList<>(),
                null,
                null
        );

        User result = this.modelMapper.fromUserAddModel(this.userAddModel);

        assertEquals(user, result);

    }

}
