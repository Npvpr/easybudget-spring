package com.easybudget.easybudget_spring.user;

import lombok.Builder;
import lombok.Getter;

// Without Getter, [org.springframework.web.HttpMediaTypeNotAcceptableException: No acceptable representation]
// is thrown when trying to return this DTO in a controller.
@Getter
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private Role role;
    private AuthProvider authProvider;
}
