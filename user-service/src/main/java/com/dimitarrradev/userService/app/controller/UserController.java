package com.dimitarrradev.userService.app.controller;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.controller.binding.UserEditModel;
import com.dimitarrradev.userService.app.role.RoleModel;
import com.dimitarrradev.userService.app.role.service.RoleService;
import com.dimitarrradev.userService.app.user.UserModel;
import com.dimitarrradev.userService.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<UserModel> createUser(@RequestBody UserAddModel userAddModel) {
        UserModel user = userService.createUser(userAddModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{id}")
    public UserModel getUser(@PathVariable Long id) {
        return  userService.getUser(id);
    }

    @GetMapping("/{id}/roles")
    public CollectionModel<RoleModel> getRoles(@PathVariable Long id) {
        return roleService.getUserRoles(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long id,
            @RequestBody UserEditModel userEditModel
    ) {
        userService.updateUser(id, userEditModel);

        return ResponseEntity.noContent().build();
    }
}
