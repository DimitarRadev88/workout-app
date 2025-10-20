package com.dimitarrradev.exercisesApi.role.dao;

import com.dimitarrradev.exercisesApi.role.Role;
import com.dimitarrradev.exercisesApi.role.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleType(RoleType roleType);
}
