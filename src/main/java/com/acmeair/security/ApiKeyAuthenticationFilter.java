package com.acmeair.security;

import com.acmeair.service.ApiTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-API-Key";
    private final ApiTokenService apiTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiKeyAuthenticationFilter(ApiTokenService apiTokenService) {
        this.apiTokenService = apiTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null || apiKey.trim().isEmpty()) {
            sendUnauthorizedResponse(response, "API key is required");
            return;
        }

        if (!apiTokenService.isValidToken(apiKey)) {
            sendUnauthorizedResponse(response, "Invalid API key");
            return;
        }

        ApiKeyAuthentication authentication = new ApiKeyAuthentication(apiKey, true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, Object> errorResponse = Map.of(
                "error", "Unauthorized",
                "message", message,
                "timestamp", java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                        .format(java.time.format.DateTimeFormatter.ISO_INSTANT) + " (UTC)"
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}