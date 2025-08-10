package com.acmeair.config;

import com.acmeair.security.ApiKeyAuthenticationFilter;
import com.acmeair.service.ApiTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ApiTokenService apiTokenService;

    public SecurityConfig(ApiTokenService apiTokenService) {
        this.apiTokenService = apiTokenService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new ApiKeyAuthenticationFilter(apiTokenService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}