package com.example.API;

import com.example.entity.user.User;
import com.example.model.returns.ReturnItemRequest;
import com.example.model.returns.ReturnRequest;
import com.example.model.returns.ReturnResponse;
import com.example.repository.UserRepository;
import com.example.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnAPI {
    private final ReturnService returnService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ReturnResponse> createReturn(@RequestBody ReturnRequest request, Principal principal) {
        Integer userId = null;

        // Lấy userId từ username trong token
        if (principal != null) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username);
            if (user != null) {
                userId = user.getId(); // Đây là ID của nhân viên xử lý trả hàng
            } else {
                return ResponseEntity.badRequest().build(); // Không tìm thấy user
            }
        } else {
            return ResponseEntity.status(401).build(); // Không có thông tin người dùng
        }

        // Gọi service xử lý tạo phiếu trả hàng
        ReturnResponse response = returnService.createReturn(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReturnResponse>> getAllReturns(Principal principal) {
        ResponseEntity<List<ReturnResponse>> responseEntity = ResponseEntity.ok(returnService.getAllReturns());
        return ResponseEntity.ok(responseEntity.getBody());
    }
}

