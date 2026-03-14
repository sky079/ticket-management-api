package com.ticket.ticketmanagement.controller;

import com.ticket.ticketmanagement.dto.AuthResponse;
import com.ticket.ticketmanagement.dto.LoginRequest;
import com.ticket.ticketmanagement.dto.RegisterRequest;
import com.ticket.ticketmanagement.entity.User;
import com.ticket.ticketmanagement.exception.InvalidCredentialsException;
import com.ticket.ticketmanagement.security.JwtUtil;
import com.ticket.ticketmanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the specified role")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
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
    @Operation(summary = "Login", description = "Authenticates user and returns JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!userService.checkPassword(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
    }
}