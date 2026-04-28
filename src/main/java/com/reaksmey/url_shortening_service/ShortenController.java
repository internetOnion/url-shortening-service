package com.reaksmey.url_shortening_service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shorten")
public class ShortenController {

    private final ShortenService shortenService;

    @PostMapping
    public ResponseEntity<ShortenBasicResponse> createShortenUrl(
        @Valid @RequestBody ShortenRequest request
    ) {
        var response = shortenService.createShortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{shortCode}")
    public ResponseEntity<ShortenBasicResponse> getShortenUrl(
        @PathVariable @NotBlank String shortCode
    ) {
        var response = shortenService.findShortenUrlByShortCode(shortCode);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{shortCode}")
    public ResponseEntity<ShortenBasicResponse> updateShortenUrl(
        @PathVariable @NotBlank String shortCode,
        @Valid @RequestBody ShortenRequest request
    ) {
        var response = shortenService.updateShortenUrl(shortCode, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{shortCode}")
    public ResponseEntity<Void> deleteShortenUrl(
        @PathVariable @NotBlank String shortCode
    ) {
        shortenService.deleteShortenUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{shortCode}/stats")
    public ResponseEntity<ShortenStatsResponse> getShortenStats(
        @PathVariable @NotBlank String shortCode
    ) {
        var response = shortenService.findShortenStatsByShortCode(shortCode);
        return ResponseEntity.ok(response);
    }
}
