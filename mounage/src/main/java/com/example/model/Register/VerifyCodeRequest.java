package com.example.model.Register;

import lombok.Data;

@Data
public class VerifyCodeRequest {
    private String email;
    private String token;
}