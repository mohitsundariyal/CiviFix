package com.civifix.civifix_backend.controller;


import com.civifix.civifix_backend.dto.LoginRequest;
import com.civifix.civifix_backend.dto.RegisterRequest;
import com.civifix.civifix_backend.entity.User;
import com.civifix.civifix_backend.security.JwtUtil;
import com.civifix.civifix_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    AuthController(AuthService authService,JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(@Valid @RequestBody RegisterRequest request){
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Registration Successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        String token = jwtUtil.genrateJwtToken(user);
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "fullName", user.getFullName(),
                "role", user.getRole().name(),
                "token",token
        ));
    }


}
