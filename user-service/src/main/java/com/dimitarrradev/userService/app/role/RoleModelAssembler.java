package com.dimitarrradev.userService.app.role;

import com.dimitarrradev.userService.app.controller.UserController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class RoleModelAssembler extends RepresentationModelAssemblerSupport<Role, RoleModel> {

    public RoleModelAssembler() {
        super(UserController.class, RoleModel.class);
    }

    @Override
    public RoleModel toModel(Role entity) {
        return new RoleModel(entity.getRoleType());
    }

}
