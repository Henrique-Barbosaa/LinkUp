package com.linkup.dto;

import java.time.LocalDateTime;

public record UrlResponse(
    String shortenUrl,
    String originalUrl,
    LocalDateTime createdAt
) {}
