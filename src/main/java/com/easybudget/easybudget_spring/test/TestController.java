package com.easybudget.easybudget_spring.test;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/authentication")
    public String userEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        System.out.println("Authentication Principal: " + authentication.getPrincipal());
        System.out.println("Authentication Name: " + authentication.getName());
        System.out.println("Authentication Authorities: " + authentication.getAuthorities());
        System.out.println("Authentication Credentials: " + authentication.getCredentials());
        System.out.println("Authentication Details: " + authentication.getDetails());

        return "Authentication Test";
    }
}
