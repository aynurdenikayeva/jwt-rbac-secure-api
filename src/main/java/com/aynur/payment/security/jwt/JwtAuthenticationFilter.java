package com.aynur.payment.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final JwtBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Əgər artıq auth varsa, yenidən set etməyək
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7).trim();

            try {
                // Redis down olsa belə isBlacklisted fail-open edir
                if (!blacklistService.isBlacklisted(token)) {

                    Claims claims = jwtService.parseClaims(token);
                    String userId = claims.getSubject();

                    // roles claim -> tam təhlükəsiz parse (List<?> ola bilər)
                    Object rawRoles = claims.get("roles");
                    List<String> roles = rawRoles == null
                            ? List.of()
                            : ((List<?>) rawRoles).stream().map(String::valueOf).toList();

                    var authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    var authentication = new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Bu log tam stacktrace verəcək -> real səbəbi görəcəksən
                log.warn("JWT parse/auth failed", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}