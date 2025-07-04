package com.easybudget.easybudget_spring.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easybudget.easybudget_spring.user.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @PostMapping("/signin")
    public String signinUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        // To understand the flow
        System.out.println("Inside AuthController: signinUser");

        return userService.authenticateUser(loginRequest);
    }

    // Fix this: org.springframework.http.converter.HttpMessageNotReadableException
    // is returning
    // on Postman as Unauthorized
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequestDto registerRequest) {
        
        System.out.println("Inside AuthController: registerUser");

        try {
            return new ResponseEntity<>(userService.createUser(registerRequest), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

}
