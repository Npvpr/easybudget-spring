package com.easybudget.easybudget_spring.auth;

import com.easybudget.easybudget_spring.user.AuthProvider;
import com.easybudget.easybudget_spring.user.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDto {
    // NotBlank also includes null check
    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Username cannot be blank.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
    private String password;

    private Role role;
    private AuthProvider authProvider;
}
