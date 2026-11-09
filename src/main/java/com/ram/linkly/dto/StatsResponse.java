package com.ram.linkly.dto;

import com.ram.linkly.entity.ShortUrl;

import java.time.Instant;

public record StatsResponse(
        String code,
        String url,
        long totalClicks,
        Instant lastClickedAt
) {
    public static StatsResponse from(ShortUrl shortUrl, long pendingClicks) {
        return new StatsResponse(
                shortUrl.getCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getClickCount() + pendingClicks,
                shortUrl.getLastClickedAt()
        );
    }
}