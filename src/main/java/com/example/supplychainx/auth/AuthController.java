package com.example.supplychainx.auth;

import com.example.supplychainx.auth.dto.AuthResponse;
import com.example.supplychainx.auth.dto.LoginRequest;
import com.example.supplychainx.auth.dto.TokenRefreshRequest;
import com.example.supplychainx.auth.dto.TokenRefreshResponse;
import com.example.supplychainx.auth.enity.RefreshToken;
import com.example.supplychainx.auth.service.AuthService;
import com.example.supplychainx.auth.service.RefreshTokenService;
import com.example.supplychainx.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService service, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.service = service;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(user); // Nouveau Access Token
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.FORBIDDEN, "Refresh token is not in database!"));
    }
}

