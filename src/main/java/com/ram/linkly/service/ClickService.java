package com.ram.linkly.service;

import com.ram.linkly.analytics.UserAgentParser;
import com.ram.linkly.analytics.UserAgentParser.ParsedAgent;
import com.ram.linkly.entity.ClickEvent;
import com.ram.linkly.entity.ShortUrl;
import com.ram.linkly.repository.ClickEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class ClickService {

    private final ClickEventRepository clickEventRepository;
    private final UserAgentParser userAgentParser;

    @Transactional
    public void record(ShortUrl shortUrl, String ip, String userAgent, String referrer) {
        ParsedAgent agent = userAgentParser.parse(userAgent);

        ClickEvent event = new ClickEvent();
        event.setShortUrl(shortUrl);
        event.setClickedAt(Instant.now());
        event.setIpHash(hash(ip));
        event.setBrowser(agent.browser());
        event.setOperatingSystem(agent.operatingSystem());
        event.setDeviceType(agent.deviceType());
        event.setReferrer(referrer);

        clickEventRepository.save(event);
    }

    // Stores a hash of the IP, never the raw address (privacy)
    private String hash(String ip) {
        if (ip == null) {
            return null;
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(ip.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}