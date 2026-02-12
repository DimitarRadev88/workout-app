package com.dimitarrradev.userService.app.user;

import com.dimitarrradev.userService.app.controller.UserController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserModelAssembler extends RepresentationModelAssemblerSupport<User, UserModel> {


    public UserModelAssembler() {
        super(UserController.class, UserModel.class);
    }

    @Override
    public UserModel toModel(User entity) {
        return new UserModel(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getWeight(),
                entity.getHeight(),
                entity.getBmi(),
                entity.getGym(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
