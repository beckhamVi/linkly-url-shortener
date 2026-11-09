package com.ram.linkly.service;

import com.ram.linkly.dto.StatsResponse;
import com.ram.linkly.entity.ShortUrl;
import com.ram.linkly.exception.NotFoundException;
import com.ram.linkly.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final ShortUrlRepository shortUrlRepository;
    private final ClickCounterService clickCounterService;

    @Transactional(readOnly = true)
    public StatsResponse getStats(String code) {
        ShortUrl shortUrl = shortUrlRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("short URL not found: " + code));

        return StatsResponse.from(shortUrl, clickCounterService.pendingClicks(code));
    }
}