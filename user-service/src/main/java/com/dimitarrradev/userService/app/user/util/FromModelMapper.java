package com.dimitarrradev.userService.app.user.util;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.user.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class FromModelMapper {

    public User fromUserAddModel(UserAddModel userAddModel) {
        return new User(null,
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
    }

}
