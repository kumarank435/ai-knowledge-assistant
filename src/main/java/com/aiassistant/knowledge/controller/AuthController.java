package com.aiassistant.knowledge.controller;

import com.aiassistant.knowledge.dto.LoginRequest;
import com.aiassistant.knowledge.dto.RegisterRequest;
import com.aiassistant.knowledge.entity.User;
import com.aiassistant.knowledge.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        return Map.of(
                "message", "Registration successful",
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole()
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        return Map.of(
                "message", "Login successful",
                "username", request.getUsername(),
                "token", token
        );
    }
}