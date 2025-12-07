package com.hng.miniapik.controller;

import com.hng.miniapik.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Protected Resources", description = "Protected endpoints with different access requirements")
public class ProtectedController {

    @GetMapping("/user-only")
    @Operation(
            summary = "User-only endpoint",
            description = "Only accessible with JWT Bearer token (not API keys)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ApiResponse<Map<String, String>>> userOnly() {
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();

        if (auth.startsWith("service:")) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("This endpoint is for authenticated users only"));
        }

        Map<String, String> data = new HashMap<>();
        data.put("message", "Hello User!");
        data.put("user", auth);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/service-only")
    @Operation(
            summary = "Service-only endpoint",
            description = "Only accessible with API key (not JWT tokens)",
            security = @SecurityRequirement(name = "API Key Authentication"))
    public ResponseEntity<ApiResponse<Map<String, String>>> serviceOnly() {
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!auth.startsWith("service:")) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("This endpoint is for services only"));
        }

        Map<String, String> data = new HashMap<>();
        data.put("message", "Hello Service!");
        data.put("service", auth.substring(8));
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/both")
    @Operation(
            summary = "Endpoint for both users and services",
            description = "Accessible with either JWT Bearer token or API key")
    public ResponseEntity<ApiResponse<Map<String, String>>> both() {
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String, String> data = new HashMap<>();
        data.put("message", "Accessible by both users and services");
        data.put("authenticated_as", auth);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
