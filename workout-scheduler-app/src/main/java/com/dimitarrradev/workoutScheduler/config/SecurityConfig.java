package com.dimitarrradev.workoutScheduler.config;

import com.dimitarrradev.workoutScheduler.userDetails.WorkoutSchedulerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final WorkoutSchedulerUserDetailsService userDetailsService;

    public SecurityConfig(WorkoutSchedulerUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(request -> request
                        .requestMatchers("/users/login", "/users/register", "/resources/**", "/css/**", "/img/**", "/js/**", "/images/**", "/bootstrap/**").permitAll()
                        .anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .formLogin(login -> login
                        .loginPage("/users/login").permitAll()
                        .defaultSuccessUrl("/", true)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
