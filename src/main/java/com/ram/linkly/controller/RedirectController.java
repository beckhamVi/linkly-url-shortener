package com.ram.linkly.controller;

import com.ram.linkly.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RedirectController {
    private final UrlService urlService;

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code){
        String originalUrl = urlService.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(originalUrl).toString())
                .build();
    }

}
