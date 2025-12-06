package com.example.miniapik.filter;

import com.example.miniapik.model.ApiKey;
import com.example.miniapik.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private final ApiKeyService apiKeyService;

    public ApiKeyAuthFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // If already authenticated (e.g. JWT filter before), skip.
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String authHeader = request.getHeader("Authorization");
            String key = null;
            if (authHeader != null) {
                if (authHeader.startsWith("Api-Key ")) {
                    key = authHeader.substring("Api-Key ".length()).trim();
                } else if (authHeader.startsWith("Bearer ")) {
                    // JWT; let other filters handle
                } else {
                    // If Authorization header present but not bearer, maybe it's raw API key
                    // we don't treat it
                }
            }
            if (key == null) {
                // fallback to X-API-KEY header
                key = request.getHeader("X-API-KEY");
            }

            if (key != null && !key.isBlank()) {
                Optional<ApiKey> maybe = apiKeyService.findByKey(key);
                if (maybe.isPresent()) {
                    ApiKey apiKey = maybe.get();
                    if (!apiKey.isRevoked() && apiKey.getExpiresAt() != null && apiKey.getExpiresAt().isAfter(Instant.now())) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken("service:" + apiKey.getId(), null,
                                        List.of(new SimpleGrantedAuthority("ROLE_SERVICE")));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        // invalid/expired/revoked - don't set authentication
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

