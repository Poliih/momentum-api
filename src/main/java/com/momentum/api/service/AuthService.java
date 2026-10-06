package com.momentum.api.service;

import com.momentum.api.dto.auth.*;
import com.momentum.api.entity.Goal;
import com.momentum.api.entity.PomodoroSettings;
import com.momentum.api.entity.User;
import com.momentum.api.exception.ApiException;
import com.momentum.api.repository.GoalRepository;
import com.momentum.api.repository.PomodoroSettingsRepository;
import com.momentum.api.repository.UserRepository;
import com.momentum.api.security.JwtService;
import com.momentum.api.security.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final PomodoroSettingsRepository pomodoroSettingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("Ja existe uma conta com este email");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        user = userRepository.save(user);

        goalRepository.save(Goal.builder().userId(user.getId()).build());
        pomodoroSettingsRepository.save(PomodoroSettings.builder().userId(user.getId()).build());

        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> ApiException.unauthorized("Email ou senha invalidos"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw ApiException.unauthorized("Email ou senha invalidos");
        }

        return issueTokens(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();

        if (!jwtService.isTokenValid(token) || jwtService.extractTokenType(token) != TokenType.REFRESH) {
            throw ApiException.unauthorized("Refresh token invalido ou expirado");
        }

        User user = userRepository.findById(jwtService.extractUserId(token))
                .orElseThrow(() -> ApiException.unauthorized("Usuario nao encontrado"));

        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());
        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getName(), user.getEmail());
    }
}
