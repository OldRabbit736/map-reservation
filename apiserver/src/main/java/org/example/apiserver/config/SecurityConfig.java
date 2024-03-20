package org.example.apiserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    // SpringDocConfig의 Bean으로부터 받아오는 값
    private final String swaggerPath;
    private final String apiDocPath;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeHttpRequest -> authorizeHttpRequest
                        .requestMatchers(swaggerPath).permitAll()
                        .requestMatchers(apiDocPath).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
