package com.ram.linkly.service;

import com.ram.linkly.dto.CreateUrlRequest;
import com.ram.linkly.entity.ShortUrl;
import com.ram.linkly.exception.NotFoundException;
import com.ram.linkly.repository.ShortUrlRepository;
import com.ram.linkly.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final ShortUrlRepository shortUrlRepository;

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
        ShortUrl shortUrl = shortUrlRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("short URL not found" + code));
        if (shortUrl.getExpiresAt() != null && !shortUrl.getExpiresAt().isAfter(Instant.now())){
            throw new NotFoundException("short URL expired: " + code);
        }
        return shortUrl.getOriginalUrl();
    }
}
