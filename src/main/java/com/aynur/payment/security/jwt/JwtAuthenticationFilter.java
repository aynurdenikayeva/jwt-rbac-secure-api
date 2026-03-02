package com.aynur.payment.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            try {
                if (!blacklistService.isBlacklisted(token)) {
                    Claims claims = jwtService.parseClaims(token);
                    String userId = claims.getSubject();

                    List<String> roles = claims.get("roles", List.class);
                    var authorities = roles == null ? List.<SimpleGrantedAuthority>of()
                            : roles.stream().map(SimpleGrantedAuthority::new).toList();

                    // principal: userId saxlayırıq (sadəlik üçün)
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignored) {
                // token səhvdirsə sadəcə auth vermirik, SecurityConfig özü 401/403 edəcək
            }
        }

        filterChain.doFilter(request, response);
    }
}