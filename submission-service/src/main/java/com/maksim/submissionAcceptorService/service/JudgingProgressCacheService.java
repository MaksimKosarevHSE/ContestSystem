package com.maksim.submissionAcceptorService.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JudgingProgressCacheService {

    private final StringRedisTemplate redisTemplate;

    private static final String STATUS_KEY_PREFIX = "submission:status:";

    private static final Duration STATUS_TTL = Duration.ofMinutes(30);

    @Async
    public void cacheTestNumAsync(Long submissionId, Integer testNum) {
        try {
            String key = STATUS_KEY_PREFIX + submissionId;
            redisTemplate.opsForValue().set(key, String.valueOf(testNum), STATUS_TTL);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public Optional<Integer> getCachedTestNum(Long submissionId) {
        try {
            String key = STATUS_KEY_PREFIX + submissionId;
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) return Optional.empty();
            return Optional.of(Integer.valueOf(value));
        } catch (Exception ex) {
           log.error(ex.getMessage());
        }
        return Optional.empty();
    }
}
