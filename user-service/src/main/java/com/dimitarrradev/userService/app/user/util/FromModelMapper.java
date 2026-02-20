package com.dimitarrradev.userService.app.user.util;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.controller.binding.UserEditModel;
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

    public User fromUserEditModel(User user, UserEditModel userEditModel) {
        user.setFirstName(userEditModel.firstName());
        user.setLastName(userEditModel.lastName());
        user.setWeight(userEditModel.weight() != null && userEditModel.weight() > 0 ? userEditModel.weight(): user.getWeight());
        user.setHeight(userEditModel.height() != null && userEditModel.height() > 0 ? userEditModel.height(): user.getHeight());
        user.setGym(userEditModel.gym() != null && !userEditModel.gym().isBlank() ? userEditModel.gym(): user.getGym());
        return user;
    }

}
