package com.example.miniapik.service;

import com.example.miniapik.model.ApiKey;
import com.example.miniapik.repository.ApiKeyRepository;
import com.example.miniapik.util.KeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class ApiKeyService {
    private final ApiKeyRepository repo;
    private final int defaultExpiryDays;

    public ApiKeyService(ApiKeyRepository repo, @Value("${app.apikey.expiration-days:30}") int defaultExpiryDays) {
        this.repo = repo;
        this.defaultExpiryDays = defaultExpiryDays;
    }

    public ApiKey createKey(String name, Integer expireDays) {
        int days = (expireDays != null) ? expireDays : defaultExpiryDays;
        String key = KeyGenerator.generateApiKey(32); // 32 bytes => ~43 chars base64-url
        Instant expiresAt = Instant.now().plus(days, ChronoUnit.DAYS);
        ApiKey apiKey = new ApiKey(key, name, expiresAt);
        return repo.save(apiKey);
    }

    public Optional<ApiKey> findByKey(String keyValue) {
        return repo.findByKeyValue(keyValue);
    }

    public void revoke(ApiKey apiKey) {
        apiKey.setRevoked(true);
        repo.save(apiKey);
    }

    public ApiKeyRepository getRepository() { return repo; }

    public void revokeById(Long id) {
        repo.findById(id).ifPresent(k -> {
            k.setRevoked(true);
            repo.save(k);
        });
    }

}

