package com.example.API;

import com.example.Config.JwtUtils;

import com.example.model.TokenRequest;
import com.example.model.TokenResponse;
import com.example.model.UserUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthApi {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody TokenRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserUpdate userAdapter = (UserUpdate) authentication.getPrincipal();
        List<String> roles = userAdapter.getAuthorities().stream()
                .map(auth -> {
                    String authority = auth.getAuthority();
                    // Remove ROLE_ prefix if it exists before storing in token
                    return authority.startsWith("ROLE_") ? authority.substring(5) : authority;
                })
                .toList();

        String token = jwtUtils.generateToken(userAdapter.getUsername(), roles);
//        String token = jwtUtils.generateToken(userAdapter.getUsername());
        TokenResponse response = new TokenResponse();
        response.setToken(token);
        response.setExpired(new Date().getTime() + jwtUtils.getExpiration());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }}
