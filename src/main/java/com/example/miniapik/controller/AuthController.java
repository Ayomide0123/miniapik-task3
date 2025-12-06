package com.example.miniapik.controller;

import com.example.miniapik.dto.AuthRequest;
import com.example.miniapik.dto.AuthResponse;
import com.example.miniapik.model.AppUser;
import com.example.miniapik.service.JwtService;
import com.example.miniapik.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthRequest req) {
        AppUser u = userService.signup(req.getUsername(), req.getPassword());
        return ResponseEntity.ok("user created: " + u.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        return userService.findByUsername(req.getUsername())
                .map(user -> {
                    if (userService.verifyPassword(user, req.getPassword())) {
                        String token = jwtService.generateToken(user.getUsername(), user.getRole());
                        return ResponseEntity.ok(new AuthResponse(token));
                    } else {
                        return ResponseEntity.status(401).body("Invalid credentials");
                    }
                })
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }
}
