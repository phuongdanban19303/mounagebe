package com.example.service;

import com.example.model.Register.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void registerUser(RegisterRequest request);
    void resetPassword(String email, String newPassword);

}
