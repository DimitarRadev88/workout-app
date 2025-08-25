package com.dimitarrradev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(request -> request
                        .requestMatchers("/", "/login", "/logout", "/register", "/resources/**").permitAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .successForwardUrl("/home"));

        return http.build();
    }

}
