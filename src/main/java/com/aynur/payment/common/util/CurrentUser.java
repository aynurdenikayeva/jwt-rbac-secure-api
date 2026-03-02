package com.aynur.payment.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUser {

    // JwtAuthenticationFilter principal-ə userId (String) qoyur
    public static Long id() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getPrincipal() == null) return null;
        return Long.valueOf(a.getPrincipal().toString());
    }
}