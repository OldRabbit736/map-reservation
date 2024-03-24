package org.example.apiserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    // SpringDocConfig의 Bean으로부터 받아오는 값
    private final String swaggerPath;
    private final String apiDocPath;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeHttpRequest -> authorizeHttpRequest
                        .requestMatchers(swaggerPath).permitAll()
                        .requestMatchers(apiDocPath).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members").anonymous()
                        .requestMatchers(HttpMethod.GET, "/api/csrf-token").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(Customizer.withDefaults()) // TODO: CSRF 인증 실패 응답 커스터마이징 하기
                .formLogin(
                        customizer -> customizer
                                .loginProcessingUrl("/login")
                                .successHandler(authenticationSuccessHandler)
                                .failureHandler(authenticationFailureHandler)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
