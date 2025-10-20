package com.dimitarrradev.exercisesApi.role;

import com.dimitarrradev.exercisesApi.role.dao.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role findByName(String name) {
        return roleRepository.findByRoleType(RoleType.valueOf(name)).orElse(null);
    }
}
