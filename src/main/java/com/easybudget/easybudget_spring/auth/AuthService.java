package com.easybudget.easybudget_spring.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.Role;
import com.easybudget.easybudget_spring.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        return AuthResponse.builder()
                .build();
    }
}

