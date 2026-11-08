package com.ram.linkly.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "click_events")
@Getter
@Setter
@NoArgsConstructor
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "short_url_id", nullable = false)
    private ShortUrl shortUrl;

    @Column(name = "clicked_at", nullable = false)
    private Instant clickedAt = Instant.now();

    @Column(name = "ip_hash", length = 64)
    private String ipHash;

    @Column(length = 64)
    private String browser;

    @Column(name = "operating_system", length = 64)
    private String operatingSystem;

    @Column(name = "device_type", length = 32)
    private String deviceType;

    @Column(length = 512)
    private String referrer;
}