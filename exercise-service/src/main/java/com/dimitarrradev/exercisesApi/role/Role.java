package com.dimitarrradev.exercisesApi.role;

import com.dimitarrradev.exercisesApi.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
    @OneToMany(fetch = FetchType.EAGER)
    private List<User> users;

}
