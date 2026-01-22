package com.tn.server.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    // 유저 ID별로 버킷(통행권 통)을 저장하는 캐시
    private final Map<Long, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(Long userId) {
        return cache.computeIfAbsent(userId, this::createNewBucket);
    }

    private Bucket createNewBucket(Long userId) {
        // 복합 정책 설정
        // 단기 제한: 1분에 15개 (순간 트래픽 허용)
        Bandwidth shortTermLimit = Bandwidth.builder()
                .capacity(15)
                .refillGreedy(15, Duration.ofMinutes(1))
                .build();

        // 장기 제한: 하루에 100개 (일일 총량 제한)
        Bandwidth longTermLimit = Bandwidth.builder()
                .capacity(100)
                .refillGreedy(100, Duration.ofDays(1))
                .build();

        return Bucket.builder()
                .addLimit(shortTermLimit)
                .addLimit(longTermLimit)
                .build();
    }
}