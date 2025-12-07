package com.hng.miniapik.controller;

import com.hng.miniapik.dto.ApiKeyRequest;
import com.hng.miniapik.dto.ApiKeyResponse;
import com.hng.miniapik.dto.ApiResponse;
import com.hng.miniapik.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/keys")
@Tag(name = "API Keys", description = "API key management for service-to-service authentication")
@SecurityRequirement(name = "Bearer Authentication")
public class ApiKeyController {
    @Autowired
    private ApiKeyService apiKeyService;

    @PostMapping("/create")
    @Operation(summary = "Create API key", description = "Generate a new API key for service-to-service access")
    public ResponseEntity<ApiResponse<ApiKeyResponse>> createApiKey(
            @Valid @RequestBody ApiKeyRequest request) {
        try {
            ApiKeyResponse response = apiKeyService.createApiKey(request);
            return ResponseEntity.ok(ApiResponse.success(response, "API Key created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-keys")
    @Operation(summary = "Get my API keys", description = "Retrieve all API keys created by the authenticated user")
    public ResponseEntity<ApiResponse<List<ApiKeyResponse>>> getMyKeys() {
        List<ApiKeyResponse> keys = apiKeyService.getMyApiKeys();
        return ResponseEntity.ok(ApiResponse.success(keys, "API Keys retrieved successfully"));
    }

    @DeleteMapping("/{keyId}/revoke")
    @Operation(summary = "Revoke API key", description = "Deactivate an API key")
    public ResponseEntity<ApiResponse<Void>> revokeKey(@PathVariable Long keyId) {
        try {
            apiKeyService.revokeApiKey(keyId);
            return ResponseEntity.ok(ApiResponse.success(null, "API Key revoked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}

