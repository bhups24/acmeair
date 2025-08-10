package com.acmeair.repository;

import com.acmeair.model.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiTokenRepository extends JpaRepository<ApiToken, String> {
    Optional<ApiToken> findByTokenValueAndActiveTrue(String tokenValue);
}