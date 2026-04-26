package com.reaksmey.url_shortening_service;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShortenService {

    private final String BASE62 =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final ShortenRepository shortenRepository;
    private final ShortenMapper shortenMapper;

    @Transactional
    public ShortenResponse createShortenUrl(ShortenRequest req) {
        String url = req.url().trim();
        String normalizeUrl = normalizeUrl(url);
        validateUrl(normalizeUrl);

        String shortCode = generateShortCode(url);
        Shorten shorten = Shorten.builder()
            .url(url)
            .shortCode(shortCode)
            .build();
        var savedShorten = shortenRepository.save(shorten);
        return shortenMapper.toResponse(savedShorten);
    }

    private String normalizeUrl(String url) {
        if (!url.startsWith("http://") || !url.startsWith("https://")) {
            return "https://" + url;
        }

        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }

        return url;
    }

    private void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();

            if (
                scheme == null ||
                (!scheme.equals("http") && !scheme.equals("https"))
            ) {
                throw new IllegalArgumentException(
                    "Only HTTP/HTTPS URLs are supported"
                );
            }

            if (uri.getHost() == null || uri.getHost().isBlank()) {
                throw new IllegalArgumentException(
                    "URL must have a valid host"
                );
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(
                "Invalid URL format: " + e.getMessage()
            );
        }
    }

    private String generateShortCode(String url) {
        Long seqNumber = shortenRepository.getNextShortCodeSeq();
        return toBase62(seqNumber);
    }

    private String toBase62(Long seqNumber) {
        StringBuilder sb = new StringBuilder();
        while (seqNumber > 0) {
            sb.append(BASE62.charAt((int) (seqNumber % 62)));
            seqNumber /= 62;
        }
        return sb.reverse().toString();
    }
}
