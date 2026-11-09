package com.ram.linkly.service;

import com.ram.linkly.dto.CreateUrlRequest;
import com.ram.linkly.entity.ShortUrl;
import com.ram.linkly.exception.NotFoundException;
import com.ram.linkly.repository.ShortUrlRepository;
import com.ram.linkly.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final ShortUrlRepository shortUrlRepository;
    //redis
    private final StringRedisTemplate redisTemplate;
    private static final String CACHE_PREFIX = "url:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    @Transactional
    public ShortUrl create(CreateUrlRequest request){
        if (request.expiresAt() != null && !request.expiresAt().isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "expires at must be in the future");
        }

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(request.url());
        shortUrl.setExpiresAt(request.expiresAt());

        if (request.alias() != null && !request.alias().isBlank()) {
            if (shortUrlRepository.existsByCode(request.alias())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "alias already exists");
            }
            shortUrl.setCode(request.alias());
            return shortUrlRepository.save(shortUrl);
        }

        shortUrl.setCode(UUID.randomUUID().toString().replace("-", ""));
        ShortUrl saved = shortUrlRepository.save(shortUrl);

        saved.setCode(Base62Encoder.encode(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public String resolve(String code){
        String cacheKey = CACHE_PREFIX + code;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        ShortUrl shortUrl = shortUrlRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("short URL not found: " + code));

        if (shortUrl.getExpiresAt() != null && !shortUrl.getExpiresAt().isAfter(Instant.now())) {
            throw new NotFoundException("short url expired " + code);
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
}
