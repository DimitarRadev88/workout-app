package com.dimitarrradev.demo.userDetails;

import com.dimitarrradev.demo.role.Role;
import com.dimitarrradev.demo.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkoutSchedulerUserDetails implements UserDetails {
    private User user;

    public WorkoutSchedulerUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (user != null) {
            List<Role> roles = user.getRoles();

            roles.forEach(
                    role -> authorities.add(
                            new SimpleGrantedAuthority(
                                    "ROLE_" + role.getRoleType().name()
                            )
                    )
            );

        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
