package com.dimitarrradev.userService.app.role.util;

import com.dimitarrradev.userService.app.role.RoleModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class RoleProcessor implements RepresentationModelProcessor<RoleModel> {
    @Override
    public RoleModel process(RoleModel model) {

        return model;
    }
}
