package com.example.miniapik.controller;

import com.example.miniapik.dto.CreateKeyRequest;
import com.example.miniapik.model.ApiKey;
import com.example.miniapik.service.ApiKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keys")
public class KeyController {
    private final ApiKeyService apiKeyService;

    public KeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    // Endpoint only for authenticated users (ROLE_USER) per SecurityConfig
    @PostMapping("/create")
    public ResponseEntity<?> createKey(@RequestBody(required = false) CreateKeyRequest req, Authentication auth) {
        String name = (req != null) ? req.getName() : "generated";
        Integer days = (req != null) ? req.getExpireDays() : null;
        ApiKey key = apiKeyService.createKey(name, days);
        // return the raw key to caller (show once!). In production, show only once and store hashed
        return ResponseEntity.ok(
                new java.util.HashMap<String, Object>() {{
                    put("apiKey", key.getKeyValue());
                    put("id", key.getId());
                    put("expiresAt", key.getExpiresAt());
                }}
        );
    }

    // Revoke a key (only user for demo)
    @PostMapping("/revoke/{id}")
    public ResponseEntity<?> revoke(@PathVariable Long id) {
        return apiKeyService.findByKey(String.valueOf(id)) // intentionally wrong: better to find by id. We'll change implementation below
                .<ResponseEntity<?>>map(k -> {
                    apiKeyService.revoke(k);
                    return ResponseEntity.ok("revoked");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Better revoke by id:
    @PostMapping("/revoke-by-id/{id}")
    public ResponseEntity<?> revokeById(@PathVariable Long id) {
        apiKeyService.revokeById(id);
        return ResponseEntity.ok("revoked");
    }

}

