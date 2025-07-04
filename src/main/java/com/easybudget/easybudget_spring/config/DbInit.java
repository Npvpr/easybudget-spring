package com.easybudget.easybudget_spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easybudget.easybudget_spring.auth.RegisterRequestDto;
import com.easybudget.easybudget_spring.user.Role;
import com.easybudget.easybudget_spring.user.UserService;

import jakarta.annotation.PostConstruct;

@Component
public class DbInit {
    @Autowired
    private UserService userService;

    @PostConstruct
    private void postConstruct() {
        // Create a guest user
        RegisterRequestDto registerRequest = RegisterRequestDto.builder()
                .email("guest@guest.guest")
                .username("Guest User")
                .password("guest1234")
                .role(Role.GUEST)
                .build();

        try {
            userService.createUser(registerRequest);
            System.out.println("Guest User created successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Guest User already exists: " + e.getMessage());
        }

    }
}
