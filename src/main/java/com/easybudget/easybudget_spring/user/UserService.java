package com.easybudget.easybudget_spring.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.auth.LoginRequestDto;
import com.easybudget.easybudget_spring.auth.RegisterRequestDto;
import com.easybudget.easybudget_spring.exception.NotFoundException;
import com.easybudget.easybudget_spring.security.JwtUtil;

@Service
public class UserService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    public String createUser(RegisterRequestDto registerRequest) {

        if (existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists! Please login.");
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

        // Connect with login to return JWT token
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .build();
        return authenticateUser(loginRequestDto);
    }

    // Check if user exists
    // Authenticate user with email and password combination
    // If user exists, generate and return JWT token
    public String authenticateUser(LoginRequestDto loginRequestDto){
        // This will run loadUserByUsername method in CustomUserDetailsService
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // this "getUsername" returns the userId, because inside loadUserByUsername,
        // userId was returned
        return jwtUtils.generateToken(userDetails.getUsername());
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
            throw new NotFoundException("No authenticated user found.");
        }
        System.out.println("Authentication: " + authentication);

        Long userId = Long.valueOf(authentication.getName());
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
    }

    public UserDto getUserInfos() {

        User user = getCurrentAuthenticatedUser();

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .authProvider(user.getAuthProvider())
                .build();
    }
}
