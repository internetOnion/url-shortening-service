package com.reaksmey.url_shortening_service;

import jakarta.validation.constraints.NotBlank;

public record ShortenRequest(
    @NotBlank(message = "URL is required") String url
) {}
