package com.ram.linkly.service;

import com.ram.linkly.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ClickCounterService {

    static final String PREFIX = "clicks:";

    private final StringRedisTemplate redisTemplate;
    private final ShortUrlRepository shortUrlRepository;
    private final TransactionTemplate transactionTemplate;

    public void increment(String code) {
        redisTemplate.opsForValue().increment(PREFIX + code);
    }

    public long pendingClicks(String code) {
        String value = redisTemplate.opsForValue().get(PREFIX + code);
        return value == null ? 0 : Long.parseLong(value);
    }

    public int syncPendingClicks() {
        int synced = 0;

        // SCAN instead of KEYS: it does not block Redis on large datasets
        ScanOptions options = ScanOptions.scanOptions().match(PREFIX + "*").count(100).build();
        try (Cursor<String> keys = redisTemplate.scan(options)) {
            while (keys.hasNext()) {
                String key = keys.next();
                String value = redisTemplate.opsForValue().get(key);
                if (value == null) {
                    continue;
                }

                long delta = Long.parseLong(value);
                if (delta <= 0) {
                    continue;
                }

                String code = key.substring(PREFIX.length());

                // Write to PostgreSQL first; only if that succeeds, remove the
                // pending amount from Redis. If the database fails, nothing is lost.
                transactionTemplate.executeWithoutResult(status ->
                        shortUrlRepository.findByCode(code).ifPresent(shortUrl -> {
                            shortUrl.setClickCount(shortUrl.getClickCount() + delta);
                            shortUrl.setLastClickedAt(Instant.now());
                        }));

                redisTemplate.opsForValue().decrement(key, delta);
                synced++;
            }
        }
        return synced;
    }
}