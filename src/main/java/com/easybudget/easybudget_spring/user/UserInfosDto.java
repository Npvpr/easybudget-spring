package com.easybudget.easybudget_spring.user;

import lombok.Builder;
import lombok.Getter;

// Without Getter, [org.springframework.web.HttpMediaTypeNotAcceptableException: No acceptable representation]
// is thrown when trying to return this DTO in a controller.
@Getter
@Builder
public class UserInfosDto {
    private String email;
    private String username;
}
