package com.dimitarrradev.demo.userDetails;

import com.dimitarrradev.demo.user.User;
import com.dimitarrradev.demo.user.UserDao;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WorkoutSchedulerUserDetailsService implements UserDetailsService {

    private UserDao userDao;

    public WorkoutSchedulerUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new WorkoutSchedulerUserDetails(user);
    }
}
