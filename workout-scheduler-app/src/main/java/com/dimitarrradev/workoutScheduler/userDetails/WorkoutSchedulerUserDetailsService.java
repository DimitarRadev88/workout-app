package com.dimitarrradev.workoutScheduler.userDetails;

import com.dimitarrradev.workoutScheduler.user.dao.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WorkoutSchedulerUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public WorkoutSchedulerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findUserByUsername(username)
                .map(WorkoutSchedulerUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
