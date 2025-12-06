package com.example.miniapik.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    public AuthResponse() {}
    public AuthResponse(String accessToken) { this.accessToken = accessToken; }
}

