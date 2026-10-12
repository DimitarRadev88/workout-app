package com.dimitarrradev.userService.app.user;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserModel extends RepresentationModel<UserModel> {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Double weight;
    private Double height;
    private Double bmi;
    private String gym;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
