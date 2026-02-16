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
        return new User(
                user.getId(),
                user.getUsername(),
                userEditModel.firstName(),
                userEditModel.lastName(),
                user.getEmail(),
                user.getPassword(),
                userEditModel.weight() != null && userEditModel.weight() > 0 ? userEditModel.weight(): user.getWeight(),
                userEditModel.height() != null && userEditModel.height() > 0 ? userEditModel.height(): user.getHeight(),
                null,
                userEditModel.gym() != null && !userEditModel.gym().isBlank() ? userEditModel.gym(): user.getGym(),
                user.getRoles(),
                user.getCreatedAt(),
                null
        );
    }

}
