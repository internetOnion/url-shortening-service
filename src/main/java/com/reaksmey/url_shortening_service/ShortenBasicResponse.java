package com.reaksmey.url_shortening_service;

import java.util.UUID;

public record ShortenBasicResponse(UUID id, String url, String shortCode) {}
