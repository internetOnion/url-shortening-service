package com.reaksmey.url_shortening_service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shorten")
public class ShortenController {

    private final ShortenService shortenService;

    @PostMapping
    public ResponseEntity<ShortenResponse> createShortenUrl(
        @Valid @RequestBody ShortenRequest request
    ) {
        var response = shortenService.createShortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
