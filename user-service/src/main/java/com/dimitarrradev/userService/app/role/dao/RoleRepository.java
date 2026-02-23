package com.dimitarrradev.userService.app.role.dao;

import com.dimitarrradev.userService.app.role.Role;
import com.dimitarrradev.userService.app.role.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findRoleByRoleType(RoleType roleType);

    Collection<Role> findAllByAndUsers_id(Long id);

}
