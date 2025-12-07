package com.hng.miniapik.repository;

import com.hng.miniapik.entity.ApiKey;
import com.hng.miniapik.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<com.hng.miniapik.entity.ApiKey> findByApiKey(String apiKey);
    List<ApiKey> findByCreatedBy_Username(String username);

    Long countByCreatedByAndActiveTrue(User user);
}
