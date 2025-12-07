package com.hng.miniapik.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ApiKeyRequest {
    @NotBlank(message = "Service name is required")
    private String serviceName;

    private String description;
    private Long expiresInDays; // null = no expiration
}
