package com.aynur.payment.user.dto;

import lombok.*;

import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private Set<String> roles;
}