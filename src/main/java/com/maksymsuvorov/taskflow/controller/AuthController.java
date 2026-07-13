package com.maksymsuvorov.taskflow.controller;

import com.maksymsuvorov.taskflow.controller.dto.AuthResponse;
import com.maksymsuvorov.taskflow.controller.dto.LoginRequest;
import com.maksymsuvorov.taskflow.controller.dto.RefreshRequest;
import com.maksymsuvorov.taskflow.controller.dto.UserCreateRequest;
import com.maksymsuvorov.taskflow.service.AuthServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceInterface authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return this.authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return this.authService.refresh(request);
    }

}
