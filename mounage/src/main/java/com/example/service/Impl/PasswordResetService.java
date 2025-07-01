package com.example.service.Impl;

import com.example.entity.user.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@Service
public class PasswordResetService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Gửi mã xác thực (OTP) đến email
     */
    public void sendResetToken(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email không tồn tại.");
        }

        String token = String.format("%06d", new Random().nextInt(999999));
        Instant expiredAt = Instant.now().plus(Duration.ofMinutes(10)); // Hết hạn sau 10 phút

        user.setResetToken(token);
        user.setResetTokenExpiredAt(expiredAt);
        user.setResetTokenUsed(false);
        userRepository.save(user);

        emailService.sendEmail(email, "Mã xác thực đổi mật khẩu", "Mã xác thực của bạn là: " + token);
    }

    /**
     * Kiểm tra mã xác thực đúng không
     */
    public void verifyResetToken(String email, String token) {
        User user = userRepository.findByEmail(email);
        if (user == null || user.getResetToken() == null) {
            throw new IllegalArgumentException("Email hoặc mã không đúng.");
        }
        if (!token.equals(user.getResetToken())) {
            throw new IllegalArgumentException("Mã xác thực không đúng.");
        }
        if (user.getResetTokenExpiredAt() == null || user.getResetTokenExpiredAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Mã xác thực đã hết hạn.");
        }
        if (Boolean.TRUE.equals(user.getResetTokenUsed())) {
            throw new IllegalArgumentException("Mã xác thực đã được sử dụng.");
        }
    }

    /**
     * Đánh dấu mã đã dùng sau khi reset mật khẩu
     */
    public void markTokenUsed(User user) {
        user.setResetTokenUsed(true);
        userRepository.save(user);
    }
}

