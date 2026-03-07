package com.aynur.payment.security;

import com.aynur.payment.security.jwt.JwtAuthenticationFilter;
import com.aynur.payment.security.rbac.AccessDeniedHandlerImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.requestCache(rc -> rc.disable());

        http.httpBasic(b -> b.disable());
        http.formLogin(f -> f.disable());
        http.exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler)
        );
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/error",
                        "/favicon.ico",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/**"
                ).permitAll()

                // public business endpoints
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/webhooks/stripe").permitAll()
                // admin endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // order creation only editor/admin
                .requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("EDITOR", "ADMIN")
                // all other order endpoints require login
                .requestMatchers("/orders/**").authenticated()
                // everything else requires login
                .anyRequest().authenticated()
        );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}