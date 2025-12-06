package com.example.miniapik.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProtectedController {

    @GetMapping("/user/me")
    public Object userMe(Authentication authentication) {
        return java.util.Map.of(
                "type", "user",
                "principal", authentication != null ? authentication.getName() : null,
                "authorities", authentication != null ? authentication.getAuthorities() : null
        );
    }

    @GetMapping("/service/ping")
    public Object servicePing(Authentication authentication) {
        return java.util.Map.of(
                "type", "service",
                "principal", authentication != null ? authentication.getName() : null,
                "authorities", authentication != null ? authentication.getAuthorities() : null
        );
    }

    @GetMapping("/both/hello")
    public Object both(Authentication authentication) {
        return java.util.Map.of(
                "who", authentication != null ? authentication.getName() : "anonymous",
                "roles", authentication != null ? authentication.getAuthorities() : null
        );
    }
}
