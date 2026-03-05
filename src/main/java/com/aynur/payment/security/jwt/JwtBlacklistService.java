package com.aynur.payment.security.jwt;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(JwtBlacklistService.class);
    private static final String PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redis;

    public void blacklist(String token, long ttlSeconds) {
        try {
            redis.opsForValue().set(PREFIX + token, "1", Duration.ofSeconds(ttlSeconds));
        } catch (RedisConnectionFailureException ex) {
            // Redis yoxdursa logout zamanı blacklist yazıla bilməyəcək
            log.warn("Redis is not reachable. Token cannot be blacklisted right now.");
            // istəsən burada RuntimeException atıb logout-u 503 eləyə bilərsən
        }
    }

    /**
     * FAIL-OPEN: Redis down olsa belə, tokeni "blacklisted deyil" qəbul edirik.
     * Yoxsa səndəki kimi hamı anonymous qalacaq və 403 alacaqsan.
     */
    public boolean isBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redis.hasKey(PREFIX + token));
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis is not reachable. Skipping blacklist check (fail-open).");
            return false;
        }
    }
}