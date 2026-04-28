package com.reaksmey.url_shortening_service.repository;

import com.reaksmey.url_shortening_service.Shorten;
import com.reaksmey.url_shortening_service.ShortenRepository;
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
    public void ShortenRepository_save_ReturnsShorten() {
        // Arrange
        String shortCode = "abc123";
        Shorten shorten = Shorten.builder()
            .shortCode(shortCode)
            .url("https://test.com")
            .build();

        // Act
        Shorten saved = shortenRepository.save(shorten);

        // Assert
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getAccessCount());
        Assertions.assertNotNull(saved.getCreatedAt());
        Assertions.assertNotNull(saved.getUpdatedAt());
        Assertions.assertEquals(saved.getAccessCount(), 0L);
        Assertions.assertEquals(shortCode, saved.getShortCode());
        Assertions.assertEquals("https://test.com", saved.getUrl());
    }

    @Test
    public void ShortenRepository_findByShortCode_ReturnShorten() {
        // Arrange
        String shortCode = "abc123";
        Shorten shorten = Shorten.builder()
            .shortCode(shortCode)
            .url("https://test.com")
            .build();
        shortenRepository.save(shorten);

        // Act
        Shorten found = shortenRepository
            .findByShortCode(shortCode)
            .orElse(null);

        // Assert
        Assertions.assertNotNull(found);
        Assertions.assertEquals(shortCode, found.getShortCode());
        Assertions.assertEquals("https://test.com", found.getUrl());
    }

    @Test
    public void ShortenRepository_findByShortCode_ReturnNull() {
        // Arrange
        String shortCode = "abc123";

        // Act
        Shorten found = shortenRepository
            .findByShortCode(shortCode)
            .orElse(null);

        // Assert
        Assertions.assertNull(found);
    }

    @Test
    public void ShortenRepository_updateUrl_ReturnUpdatedShorten() {
        // Arrange
        String shortCode = "abc123";
        Shorten shorten = Shorten.builder()
            .shortCode(shortCode)
            .url("https://test.com")
            .build();
        shortenRepository.save(shorten);

        // Act
        Shorten existedShorten = shortenRepository
            .findByShortCode(shortCode)
            .orElse(null);

        Shorten updated = null;
        if (existedShorten != null) {
            existedShorten.setUrl("https://updated.com");
            updated = shortenRepository.save(existedShorten);
        }

        // Assert
        Assertions.assertNotNull(updated);
        Assertions.assertNotNull(updated.getAccessCount());
        Assertions.assertNotNull(updated.getCreatedAt());
        Assertions.assertNotNull(updated.getUpdatedAt());
        Assertions.assertEquals(updated.getAccessCount(), 0L);
        Assertions.assertEquals(shortCode, updated.getShortCode());
        Assertions.assertEquals("https://updated.com", updated.getUrl());
    }

    @Test
    public void ShortenRepsitory_deleteByShortCode_ReturnNull() {
        // Arrange
        String shortCode = "abc123";
        Shorten shorten = Shorten.builder()
            .shortCode(shortCode)
            .url("https://test.com")
            .build();
        shortenRepository.save(shorten);

        // Act
        shortenRepository.deleteByShortCode(shortCode);
        Shorten found = shortenRepository
            .findByShortCode(shortCode)
            .orElse(null);

        // Assert
        Assertions.assertNull(found);
    }
}
