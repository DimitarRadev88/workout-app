package com.dimitarrradev.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);

    boolean existsUserByUsername(String username);
}
