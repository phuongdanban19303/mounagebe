package com.example.model.Register;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String token;
    private String newPassword;
}