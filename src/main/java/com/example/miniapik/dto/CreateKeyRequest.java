package com.example.miniapik.dto;

import lombok.Data;

@Data
public class CreateKeyRequest {
    private String name;
    private Integer expireDays; // optional override
    public CreateKeyRequest() {}
}

