package com.easybudget.easybudget_spring.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.auth.RegisterRequestDto;
import com.easybudget.easybudget_spring.exception.NotFoundException;

@Service
public class UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    public String createUser(RegisterRequestDto registerRequest) {

        if (existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Role role = registerRequest.getRole() != null ? registerRequest.getRole() : Role.USER;
        AuthProvider authProvider = registerRequest.getAuthProvider() != null ? registerRequest.getAuthProvider()
                : AuthProvider.LOCAL;

        // Plain User (Local with password)
        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(role)
                .authProvider(authProvider)
                .build();

        userRepository.save(newUser);

        // I need to connect with login here
        // So that users don't need to login again after signup
        return "New User created successfully!";
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotFoundException("No authenticated user found");
        }
        System.out.println("Authentication: " + authentication);

        Long userId = Long.valueOf(authentication.getName());
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UserInfosDto getUserInfos() {

        User user = getCurrentAuthenticatedUser();

        return UserInfosDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }
}
