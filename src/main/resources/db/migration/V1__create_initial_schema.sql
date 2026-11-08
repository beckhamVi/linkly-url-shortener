CREATE TABLE short_urls (
                            id              BIGSERIAL PRIMARY KEY,
                            code            VARCHAR(32)   NOT NULL UNIQUE,
                            original_url    VARCHAR(2048) NOT NULL,
                            created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
                            expires_at      TIMESTAMPTZ,
                            click_count     BIGINT        NOT NULL DEFAULT 0,
                            last_clicked_at TIMESTAMPTZ
);

CREATE INDEX idx_short_urls_expires_at ON short_urls (expires_at);

CREATE TABLE click_events (
                              id               BIGSERIAL PRIMARY KEY,
                              short_url_id     BIGINT       NOT NULL REFERENCES short_urls (id) ON DELETE CASCADE,
                              clicked_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                              ip_hash          VARCHAR(64),
                              browser          VARCHAR(64),
                              operating_system VARCHAR(64),
                              device_type      VARCHAR(32),
                              referrer         VARCHAR(512)
);

CREATE INDEX idx_click_events_url_time ON click_events (short_url_id, clicked_at);