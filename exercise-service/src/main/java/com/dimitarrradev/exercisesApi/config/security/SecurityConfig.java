package com.dimitarrradev.exercisesApi.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ExerciseApiUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/users/login").permitAll()
                        .requestMatchers("/exercises/add", "exercises/update", "/exercises/delete").
                        hasAnyRole("ADMINISTRATOR", "MODERATOR")
//                        .anyRequest().authenticated())
                        .anyRequest().permitAll())

                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                .userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults())
                .formLogin(login -> login
                        .loginPage("/users/login").permitAll()
//                        .usernameParameter("username")
//                        .passwordParameter("password")
                        .defaultSuccessUrl("/users/dashboard", true)
                );

        return http.build();
    }

}
