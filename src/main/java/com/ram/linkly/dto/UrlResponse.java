package com.ram.linkly.dto;

import com.ram.linkly.entity.ShortUrl;

import java.time.Instant;

public record UrlResponse(String code, String originalUrl, Instant expiresAt) {
    public static UrlResponse from(ShortUrl shortUrl) {
        return new UrlResponse(shortUrl.getCode(), shortUrl.getOriginalUrl(), shortUrl.getExpiresAt());
    }
}
