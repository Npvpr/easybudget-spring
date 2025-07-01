package com.easybudget.easybudget_spring.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.exception.NotFoundException;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    // Using UserService here will cause circular dependency issues
    @Autowired
    private UserRepository userRepository;

    // There is no loadUserByEmail method in UserDetailsService, so I used
    // loadUserByUsername
    // but it acutally loads the user by email
    // This UserDetails returns as the authentication's principal when the user is
    // authenticating (inside signin controller)
    // This is a mandatory method for authenticating users with Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws NotFoundException {
        System.out.println("Inside CustomUserDetailsService: loadUserByUsername with email: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Not Found with email: " + email));

        if (user == null) {
            throw new NotFoundException("User Not Found with email: " + email);
        }

        // Validate with email and password but return UserDetails with userId
        // Because I want to use userId as the subject of the JWT token (Emails can be
        // changed, so I think it's better to use userId in most of the workflows)
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),
                user.getPassword(),
                Collections.emptyList());
    }

    // This is an additional method to load UserDetails by Id
    public UserDetails loadUserById(Long id) throws NotFoundException {
        System.out.println("Inside CustomUserDetailsService: loadUserById with id: " + id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User Not Found with id: " + id));
        
        // Return UserDetails with userId
        // Improve: Create and use CustomUserDetails class that implements
        // UserDetails interface
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),
                user.getPassword(),
                Collections.emptyList());
    }
}
