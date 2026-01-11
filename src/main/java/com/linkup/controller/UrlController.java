package com.linkup.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkup.dto.UrlRequest;
import com.linkup.dto.UrlResponse;
import com.linkup.dto.UrlStatsResponse;
import com.linkup.service.UrlService;

import java.net.URI;

@RestController
@RequestMapping
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shortenUrl(@RequestBody @Valid UrlRequest request, HttpServletRequest httpRequest) {
        String shortCode = urlService.shortenUrl(request.originalUrl());

        String prefix = httpRequest.getRequestURL().toString().replace("/shorten", "/");
        String fullShortUrl = prefix + shortCode;

        return ResponseEntity.ok(new UrlResponse(fullShortUrl, request.originalUrl(), java.time.LocalDateTime.now()));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<UrlStatsResponse> getStats(@PathVariable String shortCode) {
        UrlStatsResponse stats = urlService.getStats(shortCode);
        return ResponseEntity.ok(stats);
    }
}