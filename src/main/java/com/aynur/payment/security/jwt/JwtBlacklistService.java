package com.aynur.payment.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private static final String PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redis;

    public void blacklist(String token, long ttlSeconds) {
        redis.opsForValue().set(PREFIX + token, "1", Duration.ofSeconds(ttlSeconds));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redis.hasKey(PREFIX + token));
    }
}