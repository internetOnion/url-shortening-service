package com.reaksmey.url_shortening_service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortenService {

    private final String BASE62 =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final ShortenRepository shortenRepository;
    private final ShortenMapper shortenMapper;

    @Transactional
    public ShortenBasicResponse createShortenUrl(ShortenRequest req) {
        String url = req.url().trim();
        String normalizeUrl = normalizeUrl(url);
        validateUrl(normalizeUrl);

        String shortCode = generateShortCode(url);
        Shorten shorten = Shorten.builder()
            .url(normalizeUrl)
            .shortCode(shortCode)
            .build();
        var savedShorten = shortenRepository.save(shorten);

        return shortenMapper.toBasicResponse(savedShorten);
    }

    @Transactional
    public ShortenBasicResponse findShortenUrlByShortCode(String shortCode) {
        Optional<Shorten> shorten = shortenRepository.findByShortCode(
            shortCode
        );

        if (shorten.isEmpty()) {
            log.debug("Resource not found for shortCode: {}", shortCode);
            throw new ResourceNotFoundException(
                "Resource not found for shortCode: " + shortCode
            );
        }

        return shortenMapper.toBasicResponse(shorten.get());
    }

    @Transactional
    public ShortenBasicResponse updateShortenUrl(
        String shortCode,
        ShortenRequest request
    ) {
        Optional<Shorten> shorten = shortenRepository.findByShortCode(
            shortCode
        );
        if (shorten.isEmpty()) {
            throw new ResourceNotFoundException(
                "Resource not found for shortCode"
            );
        }

        Shorten foundShorten = shorten.get();
        foundShorten.setUrl(request.url());
        shortenRepository.save(foundShorten);

        return shortenMapper.toBasicResponse(foundShorten);
    }

    @Transactional
    public void deleteShortenUrl(String shortCode) {
        Optional<Shorten> shorten = shortenRepository.findByShortCode(
            shortCode
        );
        if (shorten.isEmpty()) {
            throw new ResourceNotFoundException(
                "Resource not found"
            );
        }

        shortenRepository.deleteByShortCode(shortCode);
    }

    @Transactional(readOnly = true)
    public ShortenStatsResponse findShortenStatsByShortCode(String shortCode) {
        Optional<Shorten> shorten = shortenRepository.findByShortCode(
            shortCode
        );
        if (shorten.isEmpty()) {
            throw new ResourceNotFoundException(
                "Resource not found for shortCode"
            );
        }

        return shortenMapper.toShortenStatsResponse(shorten.get());
    }

    @Transactional
    public String getRedirectUrl(String shortCode) {
        Optional<Shorten> shorten = shortenRepository.findByShortCode(
            shortCode
        );

        if (shorten.isEmpty()) {
            log.debug("Resource not found for shortCode: {}", shortCode);
            throw new ResourceNotFoundException(
                "Resource not found for shortCode: " + shortCode
            );
        }

        shortenRepository.incrementAccessCount(shortCode);

        return shorten.get().getUrl();
    }

    private String normalizeUrl(String url) {
        try {
            // Check if the URL has a scheme
            URI uri = new URI(url);
            if (uri.getScheme() != null) {
                return url.endsWith("/")
                    ? url.substring(0, url.length() - 1)
                    : url;
            }
        } catch (URISyntaxException ignored) {
            // No scheme, handle below
        }
        String normalized = "https://" + url;
        return normalized.endsWith("/")
            ? normalized.substring(0, normalized.length() - 1)
            : normalized;
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
            log.error("Invalid URL format: {}", e.getMessage());
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
