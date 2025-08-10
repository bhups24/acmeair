package com.acmeair.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "api_tokens")
public class ApiToken {
    @Id
    private String id;

    @NotBlank
    @Column(name = "token_value", unique = true)
    private String tokenValue;

    @NotBlank
    private String description;

    @NotNull
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "is_active")
    private boolean active = true;

    public ApiToken() {}

    public ApiToken(String id, String tokenValue, String description, LocalDateTime createdTime, boolean active) {
        this.id = id;
        this.tokenValue = tokenValue;
        this.description = description;
        this.createdTime = createdTime;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTokenValue() { return tokenValue; }
    public void setTokenValue(String tokenValue) { this.tokenValue = tokenValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiToken apiToken = (ApiToken) o;
        return Objects.equals(id, apiToken.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}