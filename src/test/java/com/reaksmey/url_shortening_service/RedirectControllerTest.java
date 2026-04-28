package com.reaksmey.url_shortening_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RedirectControllerTest {

    @Mock
    private ShortenService shortenService;

    @InjectMocks
    private RedirectController redirectController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(redirectController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    public void redirect_ValidShortCode_ShouldReturn302WithLocationHeader()
        throws Exception {
        String shortCode = "abc123";
        String originalUrl = "https://example.com";

        Mockito.when(shortenService.getRedirectUrl(shortCode)).thenReturn(
            originalUrl
        );

        mockMvc
            .perform(get("/{shortCode}", shortCode))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", originalUrl));
    }

    @Test
    public void redirect_InvalidShortCode_ShouldReturn404() throws Exception {
        String shortCode = "nonexistent";

        Mockito.when(shortenService.getRedirectUrl(shortCode))
            .thenThrow(
                new ResourceNotFoundException(
                    "Resource not found for shortCode: " + shortCode
                )
            );

        mockMvc
            .perform(get("/{shortCode}", shortCode))
            .andExpect(status().isNotFound());
    }
}
