package com.dimitarrradev.userService.app.user.dao;

import com.dimitarrradev.userService.app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);
}
