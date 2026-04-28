package com.reaksmey.url_shortening_service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class ShortenRepositoryTest {

    @Autowired
    private ShortenRepository shortenRepository;

    @Test
    public void ShortenRepository_save_ShouldReturnsShorten() {
        String shortCode = "abc123";
        Shorten shorten = Shorten.builder()
            .shortCode(shortCode)
            .url("https://test.com")
            .build();

        Shorten saved = shortenRepository.save(shorten);

        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getAccessCount());
        Assertions.assertNotNull(saved.getCreatedAt());
        Assertions.assertNotNull(saved.getUpdatedAt());
        Assertions.assertEquals(saved.getAccessCount(), 0L);
        Assertions.assertEquals(shortCode, saved.getShortCode());
        Assertions.assertEquals("https://test.com", saved.getUrl());
    }

    @Test
    public void ShortenRepository_findByShortCode_ShouldReturnsShorten() {
        String shortCode = "abc123";
        Shorten shorten = Shorten.builder()
            .shortCode(shortCode)
            .url("https://test.com")
            .build();
        shortenRepository.save(shorten);

        Shorten found = shortenRepository
            .findByShortCode(shortCode)
            .orElse(null);

        Assertions.assertNotNull(found);
        Assertions.assertEquals(shortCode, found.getShortCode());
        Assertions.assertEquals("https://test.com", found.getUrl());
    }

    @Test
    public void ShortenRepository_findByShortCode_ShouldReturnsNull() {
        String shortCode = "abc123";

        Shorten found = shortenRepository
            .findByShortCode(shortCode)
            .orElse(null);

        Assertions.assertNull(found);
    }

    @Test
    public void ShortenRepository_updateUrl_ReturnUpdatedShorten() {
        String shortCode = "abc123";
        Shorten shorten = Shorten.builder()
            .shortCode(shortCode)
            .url("https://test.com")
            .build();
        shortenRepository.save(shorten);

        Shorten existedShorten = shortenRepository
            .findByShortCode(shortCode)
            .orElse(null);

        Shorten updated = null;
        if (existedShorten != null) {
            existedShorten.setUrl("https://updated.com");
            updated = shortenRepository.save(existedShorten);
        }

        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getAccessCount());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertEquals(updated.getAccessCount(), 0L);
        Assertions.assertEquals(shortCode, updated.getShortCode());
        Assertions.assertEquals("https://updated.com", updated.getUrl());
    }

    @Test
    public void ShortenRepsitory_deleteByShortCode_ShouldReturnsNull() {
        String shortCode = "abc123";
        Shorten shorten = Shorten.builder()
            .shortCode(shortCode)
            .url("https://test.com")
            .build();
        shortenRepository.save(shorten);

        shortenRepository.deleteByShortCode(shortCode);
        Shorten found = shortenRepository
            .findByShortCode(shortCode)
            .orElse(null);

        Assertions.assertNull(found);
    }

    @Test
    public void ShortenRepository_nextShortCodeSeq_ShouldReturnsNextSequence() {
        Long seq = shortenRepository.getNextShortCodeSeq();
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1L, seq);
    }
}
