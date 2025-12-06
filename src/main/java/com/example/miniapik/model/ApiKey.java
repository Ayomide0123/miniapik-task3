package com.example.miniapik.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "api_keys")
public class ApiKey {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 200)
    private String keyValue; // For d3m0 we store raw; in prod hash this!

    private String name; // optional friendly name
    private boolean revoked = false;
    private Instant createdAt = Instant.now();
    private Instant expiresAt;

    public ApiKey(String key, String name, Instant expiresAt) {
    }
}
