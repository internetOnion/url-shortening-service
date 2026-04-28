package com.reaksmey.url_shortening_service;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ShortenMapper {
    Shorten ToEntity(String url, String shortUrl);

    ShortenBasicResponse toBasicResponse(Shorten shorten);

    ShortenStatsResponse toShortenStatsResponse(Shorten shorten);
}
