package com.ticket.ticketmanagement.controller;

import com.ticket.ticketmanagement.dto.AuthResponse;
import com.ticket.ticketmanagement.dto.LoginRequest;
import com.ticket.ticketmanagement.dto.RegisterRequest;
import com.ticket.ticketmanagement.entity.User;
import com.ticket.ticketmanagement.security.JwtUtil;
import com.ticket.ticketmanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User.Role role = User.Role.valueOf(request.getRole().toUpperCase());
        User user = userService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                role
        );
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!userService.checkPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
    }
}