package com.ram.linkly.controller;

import com.ram.linkly.dto.CreateUrlRequest;
import com.ram.linkly.dto.StatsResponse;
import com.ram.linkly.dto.UrlResponse;
import com.ram.linkly.entity.ShortUrl;
import com.ram.linkly.repository.ShortUrlRepository;
import com.ram.linkly.service.StatsService;
import com.ram.linkly.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    private final StatsService statsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UrlResponse create(@Valid @RequestBody CreateUrlRequest request){
        ShortUrl saved = urlService.create(request);
        return UrlResponse.from(saved);
    }

    @GetMapping("/{code}/stats")
    public StatsResponse stats(@PathVariable String code) {
        return statsService.getStats(code);
    }
}
