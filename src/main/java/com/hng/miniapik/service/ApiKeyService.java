package com.hng.miniapik.service;

import com.hng.miniapik.dto.ApiKeyRequest;
import com.hng.miniapik.dto.ApiKeyResponse;
import com.hng.miniapik.entity.ApiKey;
import com.hng.miniapik.entity.User;
import com.hng.miniapik.repository.ApiKeyRepository;
import com.hng.miniapik.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApiKeyService {
    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private UserRepository userRepository;

    private static final SecureRandom secureRandom = new SecureRandom();

    public ApiKeyResponse createApiKey(ApiKeyRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String generatedKey = generateApiKey();
        // Hash before storing
        String hashedKey = hashApiKey(generatedKey);

        ApiKey apiKey = new ApiKey();
        apiKey.setApiKey(hashedKey);
        apiKey.setServiceName(request.getServiceName());
        apiKey.setDescription(request.getDescription());
        apiKey.setCreatedBy(user);
        apiKey.setActive(true);

        if (request.getExpiresInDays() != null) {
            apiKey.setExpiresAt(LocalDateTime.now().plusDays(request.getExpiresInDays()));
        }

        ApiKey saved = apiKeyRepository.save(apiKey);
        // Return response with PLAIN key (user sees it once!)
        ApiKeyResponse response = new ApiKeyResponse(
                saved.getId(),
                generatedKey, // Show plain key to user
                saved.getServiceName(),
                saved.getDescription(),
                saved.getCreatedAt(),
                saved.getExpiresAt(),
                saved.isActive()
        );

        return response;
//        return mapToResponse(saved);
    }

    public List<ApiKeyResponse> getMyApiKeys() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return apiKeyRepository.findByCreatedBy_Username(username).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void revokeApiKey(Long keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new RuntimeException("API Key not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!apiKey.getCreatedBy().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to revoke this API key");
        }

        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
    }

    private String generateApiKey() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashApiKey(String apiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(apiKey.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ApiKeyResponse mapToResponse(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getApiKey(),
                apiKey.getServiceName(),
                apiKey.getDescription(),
                apiKey.getCreatedAt(),
                apiKey.getExpiresAt(),
                apiKey.isActive()
        );
    }
}

