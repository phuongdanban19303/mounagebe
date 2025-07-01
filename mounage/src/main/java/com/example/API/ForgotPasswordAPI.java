package com.example.API;

import com.example.entity.user.User;
import com.example.model.Register.EmailRequest;
import com.example.model.Register.ResetPasswordRequest;
import com.example.model.Register.VerifyCodeRequest;
import com.example.repository.UserRepository;
import com.example.service.Impl.PasswordResetService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class ForgotPasswordAPI {
    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    /**
     * Gửi mã xác thực qua email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailRequest request) {
        try {
            passwordResetService.sendResetToken(request.getEmail());
            return ResponseEntity.ok("Đã gửi mã xác thực về email.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Xác minh mã xác thực
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest request) {
        try {
            passwordResetService.verifyResetToken(request.getEmail(), request.getToken());
            return ResponseEntity.ok("Mã xác thực hợp lệ.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Đổi mật khẩu mới
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.verifyResetToken(request.getEmail(), request.getToken());
            userService.resetPassword(request.getEmail(), request.getNewPassword());

            // Đánh dấu mã đã dùng
            User user = userRepository.findByEmail(request.getEmail());
            passwordResetService.markTokenUsed(user);

            return ResponseEntity.ok("Đổi mật khẩu thành công.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
