package com.dimitarrradev.userService.app.role;

import com.dimitarrradev.userService.app.role.enums.RoleType;
import com.dimitarrradev.userService.app.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
    @ManyToMany(mappedBy = "roles")
    private List<User> users;

}
