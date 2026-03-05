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

        // JWT -> session istifadə etmirik
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // request cache/session-a "Saved request ..." yazmasın
        http.requestCache(rc -> rc.disable());

        // default login/basic şeylərini söndür
        http.httpBasic(b -> b.disable());
        http.formLogin(f -> f.disable());

        http.exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler)
        );

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // auth + webhook public
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/webhooks/stripe").permitAll()

                // admin
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // order create yalnız editor/admin
                .requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("EDITOR", "ADMIN")

                // qalan order endpointləri authenticated
                .requestMatchers("/orders/**").authenticated()

                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}