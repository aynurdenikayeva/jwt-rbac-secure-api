package com.aynur.payment.user.controller;

import com.aynur.payment.common.exception.UnauthorizedException;
import com.aynur.payment.common.response.ApiResponse;
import com.aynur.payment.domain.entity.User;
import com.aynur.payment.domain.repository.UserRepository;
import com.aynur.payment.security.jwt.JwtBlacklistService;
import com.aynur.payment.security.jwt.JwtService;
import com.aynur.payment.security.rbac.Roles;
import com.aynur.payment.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtBlacklistService blacklistService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new UnauthorizedException("Email already exists");
        }

        User u = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(Set.of(Roles.ROLE_VIEWER.name()))
                .build();

        u = userRepository.save(u);

        String token = jwtService.generateToken(u.getId(), u.getRoles());
        return ApiResponse.ok(AuthResponse.builder()
                .token(token)
                .userId(u.getId())
                .roles(u.getRoles())
                .build());
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest req) {

        User u = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtService.generateToken(u.getId(), u.getRoles());
        return ApiResponse.ok(AuthResponse.builder()
                .token(token)
                .userId(u.getId())
                .roles(u.getRoles())
                .build());
    }

    @PostMapping("/logout")
    public ApiResponse<Object> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing token");
        }
        String token = authHeader.substring(7);
        long ttl = jwtService.secondsUntilExpiry(token);
        blacklistService.blacklist(token, ttl);
        return ApiResponse.ok(null);
    }
}