package com.reaksmey.url_shortening_service;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ShortenControllerTest {

    @Mock
    private ShortenService shortenService;

    @InjectMocks
    private ShortenController shortenController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(shortenController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    public void createShortenUrl_ValidRequest_ShouldReturn201() throws Exception {
        var response = new ShortenBasicResponse(
            UUID.randomUUID(),
            "https://example.com",
            "a"
        );

        Mockito.when(shortenService.createShortenUrl(Mockito.any(ShortenRequest.class)))
            .thenReturn(response);

        mockMvc.perform(
                post("/shorten")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"url\":\"https://example.com\"}")
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.url").value("https://example.com"))
            .andExpect(jsonPath("$.shortCode").value("a"));
    }

    @Test
    public void createShortenUrl_InvalidRequest_ShouldReturn400() throws Exception {
        mockMvc.perform(
                post("/shorten")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getShortenUrl_ValidShortCode_ShouldReturn200() throws Exception {
        String shortCode = "a";
        var response = new ShortenBasicResponse(
            UUID.randomUUID(),
            "https://example.com",
            shortCode
        );

        Mockito.when(shortenService.findShortenUrlByShortCode(shortCode))
            .thenReturn(response);

        mockMvc.perform(get("/shorten/{shortCode}", shortCode))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://example.com"))
            .andExpect(jsonPath("$.shortCode").value(shortCode));
    }

    @Test
    public void getShortenUrl_NotFound_ShouldReturn404() throws Exception {
        String shortCode = "missing";

        Mockito.when(shortenService.findShortenUrlByShortCode(shortCode))
            .thenThrow(
                new ResourceNotFoundException(
                    "Resource not found for shortCode: " + shortCode
                )
            );

        mockMvc.perform(get("/shorten/{shortCode}", shortCode))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateShortenUrl_ValidRequest_ShouldReturn200() throws Exception {
        String shortCode = "a";
        var response = new ShortenBasicResponse(
            UUID.randomUUID(),
            "https://updated.com",
            shortCode
        );

        Mockito.when(
            shortenService.updateShortenUrl(
                Mockito.eq(shortCode),
                Mockito.any(ShortenRequest.class)
            )
        ).thenReturn(response);

        mockMvc.perform(
                put("/shorten/{shortCode}", shortCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"url\":\"https://updated.com\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://updated.com"))
            .andExpect(jsonPath("$.shortCode").value(shortCode));
    }

    @Test
    public void updateShortenUrl_NotFound_ShouldReturn404() throws Exception {
        String shortCode = "missing";

        Mockito.when(
            shortenService.updateShortenUrl(
                Mockito.eq(shortCode),
                Mockito.any(ShortenRequest.class)
            )
        ).thenThrow(
            new ResourceNotFoundException(
                "Resource not found for shortCode: " + shortCode
            )
        );

        mockMvc.perform(
                put("/shorten/{shortCode}", shortCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"url\":\"https://updated.com\"}")
            )
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShortenUrl_ValidShortCode_ShouldReturn204() throws Exception {
        String shortCode = "a";

        Mockito.doNothing().when(shortenService).deleteShortenUrl(shortCode);

        mockMvc.perform(delete("/shorten/{shortCode}", shortCode))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShortenUrl_NotFound_ShouldReturn404() throws Exception {
        String shortCode = "missing";

        Mockito.doThrow(
            new ResourceNotFoundException(
                "Resource not found for shortCode: " + shortCode
            )
        ).when(shortenService).deleteShortenUrl(shortCode);

        mockMvc.perform(delete("/shorten/{shortCode}", shortCode))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getShortenStats_ValidShortCode_ShouldReturn200() throws Exception {
        String shortCode = "a";
        var response = new ShortenStatsResponse(
            UUID.randomUUID(),
            "https://example.com",
            shortCode,
            42L
        );

        Mockito.when(shortenService.findShortenStatsByShortCode(shortCode))
            .thenReturn(response);

        mockMvc.perform(get("/shorten/{shortCode}/stats", shortCode))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://example.com"))
            .andExpect(jsonPath("$.shortCode").value(shortCode))
            .andExpect(jsonPath("$.accessCount").value(42));
    }

    @Test
    public void getShortenStats_NotFound_ShouldReturn404() throws Exception {
        String shortCode = "missing";

        Mockito.when(shortenService.findShortenStatsByShortCode(shortCode))
            .thenThrow(
                new ResourceNotFoundException(
                    "Resource not found for shortCode: " + shortCode
                )
            );

        mockMvc.perform(get("/shorten/{shortCode}/stats", shortCode))
            .andExpect(status().isNotFound());
    }
}
