package com.dimitarrradev.userService.app.role;

import com.dimitarrradev.userService.app.role.enums.RoleType;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleModel extends RepresentationModel<RoleModel> {
    private RoleType roleType;
}
