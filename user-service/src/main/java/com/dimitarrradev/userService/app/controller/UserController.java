package com.dimitarrradev.userService.app.controller;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.controller.binding.UserEditModel;
import com.dimitarrradev.userService.app.controller.binding.UserLoginModel;
import com.dimitarrradev.userService.app.controller.binding.UserPasswordChangeModel;
import com.dimitarrradev.userService.app.error.exception.InvalidRequestBodyException;
import com.dimitarrradev.userService.app.role.RoleModel;
import com.dimitarrradev.userService.app.role.service.RoleService;
import com.dimitarrradev.userService.app.security.JwtService;
import com.dimitarrradev.userService.app.security.LoginResult;
import com.dimitarrradev.userService.app.user.UserModel;
import com.dimitarrradev.userService.app.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<UserModel> createUser(
            @Valid @RequestBody UserAddModel userAddModel,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestBodyException(bindingResult);
        }

        UserModel user = userService.createUser(userAddModel);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserModel> login(
            @Valid @RequestBody UserLoginModel userLoginModel,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestBodyException(bindingResult);
        }

        LoginResult loginResult = userService.login(userLoginModel);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, jwtService.createCookie(loginResult.token()).toString())
                .body(loginResult.user());
    }

    @GetMapping("/{id}")
    public UserModel getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/{id}/roles")
    public CollectionModel<RoleModel> getRoles(@PathVariable Long id) {
        return roleService.getUserRoles(id);
    }

    @PatchMapping("/{id}")
    public UserModel updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserEditModel userEditModel,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestBodyException(bindingResult);
        }
        return userService.updateUser(id, userEditModel);
    }

    @PostMapping("/create-password-reset-token")
    public ResponseEntity<Void> postPasswordChangeRequest(@RequestParam String email) {
        userService.createPasswordResetToken(email);

        return ResponseEntity
                .created(URI.create(email))
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

    @PatchMapping("/change-password")
    public UserModel updateUserPassword(@Valid @RequestBody UserPasswordChangeModel passwordChangeModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestBodyException(bindingResult);
        }

        return userService.resetPassword(passwordChangeModel);
    }

}
