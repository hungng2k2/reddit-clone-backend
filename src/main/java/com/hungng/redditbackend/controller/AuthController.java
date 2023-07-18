package com.hungng.redditbackend.controller;

import com.hungng.redditbackend.dto.*;
import com.hungng.redditbackend.service.AuthService;
import com.hungng.redditbackend.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseObject<Object>> signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("User Registration Successful")
                        .build());
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<ResponseObject<Object>> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Account Activated Successfully")
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<AuthenticationResponse>> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(ResponseObject.<AuthenticationResponse>builder()
                .status(HttpStatus.OK)
                .message("Login Successfully")
                .data(authService.login(loginRequest))
                .build());
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<ResponseObject<AuthenticationResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(ResponseObject.<AuthenticationResponse>builder()
                .status(HttpStatus.OK)
                .message("Refresh token successfully")
                .data(authService.refreshToken(refreshTokenRequest))
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseObject<Object>> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Refresh Token Deleted Successfully!!")
                .build());
    }
}
