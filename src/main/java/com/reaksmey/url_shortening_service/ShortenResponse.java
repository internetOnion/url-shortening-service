package com.reaksmey.url_shortening_service;

import java.util.UUID;

public record ShortenResponse(
    UUID id,
    String url,
    String shortCode,
    Long accessCount
) {}
