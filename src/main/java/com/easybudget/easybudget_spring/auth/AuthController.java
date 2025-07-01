package com.easybudget.easybudget_spring.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easybudget.easybudget_spring.security.JwtUtil;
import com.easybudget.easybudget_spring.user.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtils;

    @PostMapping("/signin")
    public String authenticateUser(@RequestBody LoginRequestDto loginRequest) {
        // To understand the flow
        System.out.println("Inside AuthController: authenticateUser");

        // This will run loadUserByUsername method in CustomUserDetailsService
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // this "getUsername" returns the userId, because inside loadUserByUsername,
        // userId was returned
        return jwtUtils.generateToken(userDetails.getUsername());
    }

    // Fix this: org.springframework.http.converter.HttpMessageNotReadableException
    // is returning
    // on Postman as Unauthorized
    // Fix this: wrong inputs -> no email/ no password/ no username
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDto registerRequest) {
        
        System.out.println("Inside AuthController: registerUser");

        try {
            String result = userService.createUser(registerRequest);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

}
