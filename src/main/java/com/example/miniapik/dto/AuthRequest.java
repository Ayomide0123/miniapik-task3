package com.example.miniapik.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    public AuthRequest() {}
}
