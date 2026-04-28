package com.reaksmey.url_shortening_service;

import java.util.UUID;

public record ShortenStatsResponse(
    UUID id,
    String url,
    String shortCode,
    Long accessCount
) {}
