package com.acmeair.service;

import com.acmeair.model.ApiToken;
import com.acmeair.repository.ApiTokenRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class ApiTokenService {
    private final ApiTokenRepository apiTokenRepository;

    public ApiTokenService(ApiTokenRepository apiTokenRepository) {
        this.apiTokenRepository = apiTokenRepository;
    }

    public boolean isValidToken(String token) {
        return apiTokenRepository.findByTokenValueAndActiveTrue(token).isPresent();
    }

    public ApiToken createToken(String tokenValue, String description) {
        ApiToken token = new ApiToken();
        token.setId("TK" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        token.setTokenValue(tokenValue);
        token.setDescription(description);
        token.setCreatedTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        token.setActive(true);
        return apiTokenRepository.save(token);
    }
}