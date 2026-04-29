package com.reaksmey.url_shortening_service;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShortenServiceTest {

    @Mock
    private ShortenRepository shortenRepository;

    @Mock
    private ShortenMapper shortenMapper;

    @InjectMocks
    private ShortenService shortenService;

    @Test
    public void ShortenService_createShortenUrl_NoSchemeUrl_ShouldReturnsShortenBasicReponse() {
        var request = new ShortenRequest("example.com");
        var entity = new Shorten(
            UUID.randomUUID(),
            "https://example.com",
            "a",
            0L,
            null,
            null
        );
        var response = new ShortenBasicResponse(
            entity.getId(),
            "https://example.com",
            "a"
        );

        Mockito.when(shortenRepository.getNextShortCodeSeq()).thenReturn(1L);
        Mockito.when(
            shortenRepository.save(Mockito.any(Shorten.class))
        ).thenReturn(entity);
        Mockito.when(
            shortenMapper.toBasicResponse(Mockito.any(Shorten.class))
        ).thenReturn(response);

        ShortenBasicResponse result = shortenService.createShortenUrl(request);

        Assertions.assertEquals("https://example.com", result.url());
        Assertions.assertEquals("a", result.shortCode());
        Mockito.verify(shortenRepository).save(Mockito.any(Shorten.class));
        Mockito.verify(shortenMapper).toBasicResponse(
            Mockito.any(Shorten.class)
        );
    }

    @Test
    public void ShortenService_createShortenUrl_HttpsUrl_ShouldReturnsShortenBasicReponse() {
        var request = new ShortenRequest("https://example.com");
        var entity = new Shorten(
            UUID.randomUUID(),
            "https://example.com",
            "a",
            0L,
            null,
            null
        );
        var response = new ShortenBasicResponse(
            entity.getId(),
            "https://example.com",
            "a"
        );

        Mockito.when(shortenRepository.getNextShortCodeSeq()).thenReturn(1L);
        Mockito.when(
            shortenRepository.save(Mockito.any(Shorten.class))
        ).thenReturn(entity);
        Mockito.when(
            shortenMapper.toBasicResponse(Mockito.any(Shorten.class))
        ).thenReturn(response);

        ShortenBasicResponse result = shortenService.createShortenUrl(request);

        Assertions.assertEquals("https://example.com", result.url());
        Assertions.assertEquals("a", result.shortCode());
        Mockito.verify(shortenRepository).save(Mockito.any(Shorten.class));
        Mockito.verify(shortenMapper).toBasicResponse(
            Mockito.any(Shorten.class)
        );
    }

    @Test
    public void ShortenServie_createShortenUrl_HttpUrl_ShouldReturnsShortenBasicReponse() {
        var request = new ShortenRequest("http://example.com");
        var entity = new Shorten(
            UUID.randomUUID(),
            "http://example.com",
            "a",
            0L,
            null,
            null
        );
        var response = new ShortenBasicResponse(
            entity.getId(),
            "http://example.com",
            "a"
        );

        Mockito.when(shortenRepository.getNextShortCodeSeq()).thenReturn(1L);
        Mockito.when(
            shortenRepository.save(Mockito.any(Shorten.class))
        ).thenReturn(entity);
        Mockito.when(
            shortenMapper.toBasicResponse(Mockito.any(Shorten.class))
        ).thenReturn(response);

        ShortenBasicResponse result = shortenService.createShortenUrl(request);

        Assertions.assertEquals("http://example.com", result.url());
        Assertions.assertEquals("a", result.shortCode());
        Mockito.verify(shortenRepository).save(Mockito.any(Shorten.class));
        Mockito.verify(shortenMapper).toBasicResponse(
            Mockito.any(Shorten.class)
        );
    }

    @Test
    public void ShortenService_createShortenUrl_InvalidUrl_ShouldThrow() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            shortenService.createShortenUrl(new ShortenRequest("not a url"))
        );
    }

    @Test
    public void ShortenService_createShortenUrl_UnsupportedUrlSchema_ShouldThrow() {
        var ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
            shortenService.createShortenUrl(
                new ShortenRequest("ftp://example.com")
            )
        );
        Assertions.assertEquals(
            "Only HTTP/HTTPS URLs are supported",
            ex.getMessage()
        );
    }

    @Test
    public void ShortenService_findShortenUrlByShortCode_ShouldReturnsShortenBasicResponse() {
        String shortCode = "a";
        var entity = new Shorten(
            UUID.randomUUID(),
            "https://example.com",
            shortCode,
            0L,
            null,
            null
        );
        var response = new ShortenBasicResponse(
            entity.getId(),
            "https://example.com",
            shortCode
        );

        Mockito.when(shortenRepository.findByShortCode(shortCode)).thenReturn(
            Optional.of(entity)
        );
        Mockito.when(
            shortenMapper.toBasicResponse(Mockito.any(Shorten.class))
        ).thenReturn(response);

        ShortenBasicResponse result = shortenService.findShortenUrlByShortCode(
            shortCode
        );

        Assertions.assertEquals("https://example.com", result.url());
        Assertions.assertEquals(shortCode, result.shortCode());
        Mockito.verify(shortenRepository).findByShortCode(shortCode);
        Mockito.verify(shortenMapper).toBasicResponse(
            Mockito.any(Shorten.class)
        );
    }

    @Test
    public void ShortenService_updateShortenUrl_ShouldReturnsShortenBasicResponse() {
        String shortCode = "a";
        var updatedRequest = new ShortenRequest("https://updated.com");
        var entity = new Shorten(
            UUID.randomUUID(),
            "https://example.com",
            shortCode,
            0L,
            null,
            null
        );
        var response = new ShortenBasicResponse(
            entity.getId(),
            updatedRequest.url(),
            shortCode
        );

        Mockito.when(shortenRepository.findByShortCode(shortCode)).thenReturn(
            Optional.of(entity)
        );
        Mockito.when(
            shortenMapper.toBasicResponse(Mockito.any(Shorten.class))
        ).thenReturn(response);

        ShortenBasicResponse result = shortenService.updateShortenUrl(
            shortCode,
            updatedRequest
        );

        Assertions.assertEquals("https://updated.com", result.url());
        Assertions.assertEquals(shortCode, result.shortCode());
        Mockito.verify(shortenRepository).findByShortCode(shortCode);
        Mockito.verify(shortenMapper).toBasicResponse(
            Mockito.any(Shorten.class)
        );
    }

    @Test
    public void ShortenService_getRedirectUrl_ShouldReturnUrlAndIncrementAccessCount() {
        String shortCode = "a";
        var entity = new Shorten(
            UUID.randomUUID(),
            "https://example.com",
            shortCode,
            5L,
            null,
            null
        );

        Mockito.when(shortenRepository.findByShortCode(shortCode)).thenReturn(
            Optional.of(entity)
        );
        Mockito.doNothing().when(shortenRepository).incrementAccessCount(shortCode);

        String result = shortenService.getRedirectUrl(shortCode);

        Assertions.assertEquals("https://example.com", result);
        Mockito.verify(shortenRepository).incrementAccessCount(shortCode);
    }

    @Test
    public void ShortenService_getRedirectUrl_NotFound_ShouldThrow() {
        String shortCode = "missing";

        Mockito.when(shortenRepository.findByShortCode(shortCode)).thenReturn(
            Optional.empty()
        );

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
            shortenService.getRedirectUrl(shortCode)
        );
    }

    @Test
    public void ShortenService_deleteShortenUrl_ShouldDeleteShorten() {
        String shortCode = "a";
        var entity = new Shorten(
            UUID.randomUUID(),
            "https://example.com",
            shortCode,
            0L,
            null,
            null
        );

        Mockito.when(shortenRepository.findByShortCode(shortCode)).thenReturn(
            Optional.of(entity)
        );

        shortenService.deleteShortenUrl(shortCode);

        Mockito.verify(shortenRepository).deleteByShortCode(shortCode);
    }
}
