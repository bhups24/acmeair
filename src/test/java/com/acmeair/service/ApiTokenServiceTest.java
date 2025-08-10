package com.acmeair.service;

import com.acmeair.model.ApiToken;
import com.acmeair.repository.ApiTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiTokenServiceTest {

    @Mock
    private ApiTokenRepository apiTokenRepository;

    @InjectMocks
    private ApiTokenService apiTokenService;

    private ApiToken testToken;

    @BeforeEach
    void setUp() {
        testToken = new ApiToken("TK12345678", "test-token-value", "Test Token",
                LocalDateTime.now(), true);
    }

    @Test
    void isValidToken_Success_ReturnsTrueForValidActiveToken() {
        // Arrange
        when(apiTokenRepository.findByTokenValueAndActiveTrue("valid-token"))
                .thenReturn(Optional.of(testToken));

        // Act
        boolean result = apiTokenService.isValidToken("valid-token");

        // Assert
        assertThat(result).isTrue();
        verify(apiTokenRepository).findByTokenValueAndActiveTrue("valid-token");
    }

    @Test
    void isValidToken_Failure_ReturnsFalseForInvalidToken() {
        // Arrange
        when(apiTokenRepository.findByTokenValueAndActiveTrue("invalid-token"))
                .thenReturn(Optional.empty());

        // Act
        boolean result = apiTokenService.isValidToken("invalid-token");

        // Assert
        assertThat(result).isFalse();
        verify(apiTokenRepository).findByTokenValueAndActiveTrue("invalid-token");
    }

    @Test
    void isValidToken_Failure_ReturnsFalseForNullToken() {
        // Arrange
        when(apiTokenRepository.findByTokenValueAndActiveTrue(null))
                .thenReturn(Optional.empty());

        // Act
        boolean result = apiTokenService.isValidToken(null);

        // Assert
        assertThat(result).isFalse();
        verify(apiTokenRepository).findByTokenValueAndActiveTrue(null);
    }

    @Test
    void createToken_Success_CreatesAndSavesNewToken() {
        // Arrange
        when(apiTokenRepository.save(any(ApiToken.class))).thenReturn(testToken);

        // Act
        ApiToken result = apiTokenService.createToken("new-token-value", "New Test Token");

        // Assert
        assertThat(result).isNotNull();
        verify(apiTokenRepository).save(any(ApiToken.class));
    }

    @Test
    void createToken_Success_GeneratesProperTokenId() {
        // Arrange
        when(apiTokenRepository.save(any(ApiToken.class))).thenAnswer(invocation -> {
            ApiToken token = invocation.getArgument(0);
            assertThat(token.getId()).startsWith("TK");
            assertThat(token.getId()).hasSize(10); // TK + 8 characters
            assertThat(token.getTokenValue()).isEqualTo("test-value");
            assertThat(token.getDescription()).isEqualTo("Test Description");
            assertThat(token.isActive()).isTrue();
            assertThat(token.getCreatedTime()).isNotNull();
            return token;
        });

        // Act
        apiTokenService.createToken("test-value", "Test Description");

        // Assert - Verification happens in the mock answer above
        verify(apiTokenRepository).save(any(ApiToken.class));
    }
}