package com.raunak.taskmanager.service;

import com.raunak.taskmanager.dto.AuthResponse;
import com.raunak.taskmanager.dto.LoginRequest;
import com.raunak.taskmanager.dto.RegisterRequest;
import com.raunak.taskmanager.entity.Role;
import com.raunak.taskmanager.entity.User;
import com.raunak.taskmanager.exception.DuplicateResourceException;
import com.raunak.taskmanager.exception.UnauthorizedException;
import com.raunak.taskmanager.repository.UserRepository;
import com.raunak.taskmanager.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .token(jwtService.generateToken(user.getEmail(), user.getRole().name()))
                .tokenType("Bearer")
                .expiresInMs(jwtService.getExpirationMs())
                .user(AuthResponse.UserSummary.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build())
                .build();
    }
}
