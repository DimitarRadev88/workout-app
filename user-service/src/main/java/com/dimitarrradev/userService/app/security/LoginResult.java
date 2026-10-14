package com.dimitarrradev.userService.app.security;

import com.dimitarrradev.userService.app.user.UserModel;

public record LoginResult(UserModel user, String token) {
}
