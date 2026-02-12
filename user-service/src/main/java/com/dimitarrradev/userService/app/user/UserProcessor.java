package com.dimitarrradev.userService.app.user;

import com.dimitarrradev.userService.app.controller.UserController;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserProcessor implements RepresentationModelProcessor<UserModel> {

    @Override
    public UserModel process(UserModel model) {
        model.add(linkTo(methodOn(UserController.class).getUser(model.getId())).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).updateUser(model.getId(), null)).withRel("update"));
        model.add(linkTo(methodOn(UserController.class).getRoles(model.getId())).withRel("roles"));
        return model;
    }


}
