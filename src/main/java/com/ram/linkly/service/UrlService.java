package com.ram.linkly.service;

import com.ram.linkly.dto.CreateUrlRequest;
import com.ram.linkly.entity.ShortUrl;
import com.ram.linkly.exception.BadRequestException;
import com.ram.linkly.exception.ConflictException;
import com.ram.linkly.exception.GoneException;
import com.ram.linkly.exception.NotFoundException;
import com.ram.linkly.repository.ShortUrlRepository;
import com.ram.linkly.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {

    private static final String CACHE_PREFIX = "url:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    private final ShortUrlRepository shortUrlRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public ShortUrl create(CreateUrlRequest request) {
        if (request.expiresAt() != null && !request.expiresAt().isAfter(Instant.now())) {
            throw new BadRequestException("expiresAt must be in the future");
        }

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(request.url());
        shortUrl.setExpiresAt(request.expiresAt());

        if (request.alias() != null && !request.alias().isBlank()) {
            if (shortUrlRepository.existsByCode(request.alias())) {
                throw new ConflictException("alias already in use");
            }
            shortUrl.setCode(request.alias());
            return shortUrlRepository.save(shortUrl);
        }

        shortUrl.setCode(UUID.randomUUID().toString().replace("-", ""));
        ShortUrl saved = shortUrlRepository.save(shortUrl);

        saved.setCode(Base62Encoder.encode(saved.getId()));
        return shortUrlRepository.saveAndFlush(saved);
    }

    @Transactional(readOnly = true)
    public String resolve(String code) {
        String cacheKey = CACHE_PREFIX + code;

        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        ShortUrl shortUrl = shortUrlRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("short URL not found: " + code));

        if (shortUrl.getExpiresAt() != null && !shortUrl.getExpiresAt().isAfter(Instant.now())) {
            throw new GoneException("short URL expired: " + code);
        }

        Duration ttl = DEFAULT_TTL;
        if (shortUrl.getExpiresAt() != null) {
            Duration untilExpiry = Duration.between(Instant.now(), shortUrl.getExpiresAt());
            if (untilExpiry.compareTo(ttl) < 0) {
                ttl = untilExpiry;
            }
        }

        redisTemplate.opsForValue().set(cacheKey, shortUrl.getOriginalUrl(), ttl);
        return shortUrl.getOriginalUrl();
    }

    @Transactional
    public ShortUrl findActive(String code) {
        ShortUrl shortUrl = shortUrlRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("short URL not found: " + code));

        if (shortUrl.getExpiresAt() != null && !shortUrl.getExpiresAt().isAfter(Instant.now())) {
            throw new GoneException("short URL expired: " + code);
        }
        return shortUrl;
    }

}