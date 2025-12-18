package com.example.supplychainx.auth.service;

import com.example.supplychainx.auth.dto.AuthResponse;
import com.example.supplychainx.auth.dto.LoginRequest;
import com.example.supplychainx.auth.enity.RefreshToken;
import com.example.supplychainx.security.JwtService;
import com.example.supplychainx.service_approvisionnement.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService, RefreshTokenService refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse login(LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) auth.getPrincipal();

        // 4. Générez les deux tokens
        String jwt = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        // 5. Renvoyez l'objet complet
        return new AuthResponse(jwt, refreshToken.getToken());
    }
}
