package com.dimitarrradev.userService.app.controller;

import com.dimitarrradev.userService.app.controller.binding.UserAddModel;
import com.dimitarrradev.userService.app.user.User;
import com.dimitarrradev.userService.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Void> createUser(@RequestBody UserAddModel userAddModel) {
        userService.createUser(userAddModel);
        return ResponseEntity.created(URI.create("")).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return  ResponseEntity.ok(userService.getUser(id));
    }

}
