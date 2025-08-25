package com.dimitarrradev.demo.role;

import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Resource
public interface RoleDao extends JpaRepository<Role, Integer> {

    List<Role> getRolesBy();
}
