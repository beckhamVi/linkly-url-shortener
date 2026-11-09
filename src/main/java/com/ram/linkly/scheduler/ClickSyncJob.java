package com.ram.linkly.scheduler;

import com.ram.linkly.service.ClickCounterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClickSyncJob {

    private final ClickCounterService clickCounterService;

    // Runs one minute after the previous run finished
    @Scheduled(fixedDelay = 60_000)
    public void sync() {
        int synced = clickCounterService.syncPendingClicks();
        if (synced > 0) {
            log.info("Synced click counters for {} short URLs", synced);
        }
    }
}