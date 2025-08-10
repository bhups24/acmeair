package com.acmeair.repository;

import com.acmeair.model.ApiToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("ApiTokenRepository Tests")
class ApiTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApiTokenRepository apiTokenRepository;

    private ApiToken activeToken;
    private ApiToken inactiveToken;
    private ApiToken demoToken;

    @BeforeEach
    void setUp() {
        activeToken = new ApiToken(
                "TK12345678", "active-token-value-123", "Active Test Token",
                LocalDateTime.of(2025, 8, 10, 10, 0, 0), true
        );

        inactiveToken = new ApiToken(
                "TK87654321", "inactive-token-value-456", "Inactive Test Token",
                LocalDateTime.of(2025, 8, 9, 15, 30, 0), false
        );

        demoToken = new ApiToken(
                "TK11111111", "acme-air-demo-2025-secure-token-12345", "Demo API Token",
                LocalDateTime.of(2025, 8, 1, 0, 0, 0), true
        );

        entityManager.persistAndFlush(activeToken);
        entityManager.persistAndFlush(inactiveToken);
        entityManager.persistAndFlush(demoToken);
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should save a new API token successfully")
        void shouldSaveNewApiTokenSuccessfully() {
            ApiToken newToken = new ApiToken(
                    "TK99999999", "new-secure-token-789", "New Test Token",
                    LocalDateTime.now(), true
            );

            ApiToken savedToken = apiTokenRepository.save(newToken);

            assertThat(savedToken).isNotNull();
            assertThat(savedToken.getId()).isEqualTo("TK99999999");
            assertThat(savedToken.getTokenValue()).isEqualTo("new-secure-token-789");
            assertThat(savedToken.getDescription()).isEqualTo("New Test Token");
            assertThat(savedToken.isActive()).isTrue();
            assertThat(savedToken.getCreatedTime()).isNotNull();
        }

        @Test
        @DisplayName("Should find API token by ID when it exists")
        void shouldFindApiTokenByIdWhenExists() {
            Optional<ApiToken> result = apiTokenRepository.findById("TK12345678");

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo("TK12345678");
            assertThat(result.get().getTokenValue()).isEqualTo("active-token-value-123");
            assertThat(result.get().getDescription()).isEqualTo("Active Test Token");
            assertThat(result.get().isActive()).isTrue();
            assertThat(result.get().getCreatedTime()).isEqualTo(LocalDateTime.of(2025, 8, 10, 10, 0, 0));
        }

        @Test
        @DisplayName("Should return empty when API token ID does not exist")
        void shouldReturnEmptyWhenApiTokenIdDoesNotExist() {
            Optional<ApiToken> result = apiTokenRepository.findById("NONEXISTENT");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should update existing API token successfully")
        void shouldUpdateExistingApiTokenSuccessfully() {
            ApiToken token = apiTokenRepository.findById("TK12345678").orElseThrow();
            token.setActive(false);
            token.setDescription("Updated Test Token - Deactivated");

            ApiToken updatedToken = apiTokenRepository.save(token);

            assertThat(updatedToken.isActive()).isFalse();
            assertThat(updatedToken.getDescription()).isEqualTo("Updated Test Token - Deactivated");

            Optional<ApiToken> retrieved = apiTokenRepository.findById("TK12345678");
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get().isActive()).isFalse();
            assertThat(retrieved.get().getDescription()).isEqualTo("Updated Test Token - Deactivated");
        }

        @Test
        @DisplayName("Should delete API token by ID successfully")
        void shouldDeleteApiTokenByIdSuccessfully() {
            assertThat(apiTokenRepository.existsById("TK12345678")).isTrue();

            apiTokenRepository.deleteById("TK12345678");

            assertThat(apiTokenRepository.existsById("TK12345678")).isFalse();
            assertThat(apiTokenRepository.findById("TK12345678")).isEmpty();
        }

        @Test
        @DisplayName("Should delete API token entity successfully")
        void shouldDeleteApiTokenEntitySuccessfully() {
            ApiToken token = apiTokenRepository.findById("TK87654321").orElseThrow();

            apiTokenRepository.delete(token);

            assertThat(apiTokenRepository.existsById("TK87654321")).isFalse();
            assertThat(apiTokenRepository.count()).isEqualTo(2);
        }
    }
}