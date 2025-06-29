package com.easybudget.easybudget_spring.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.auth.RegisterRequest;
import com.easybudget.easybudget_spring.exception.NotFoundException;

@Service
public class UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    public String createUser(RegisterRequest registerRequest) {

        // Plain User (Local with password)
        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .authProvider(AuthProvider.LOCAL)
                .build();

        userRepository.save(newUser);

        // I need to connect with login here
        // So that users don't need to login again after signup
        return "New User created successfully!";
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
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
