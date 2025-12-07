package com.hng.miniapik.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiKeyResponse {
    private Long id;
    private String apiKey;
    private String serviceName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean active;
}
