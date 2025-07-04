package com.easybudget.easybudget_spring.account;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAccountRequestDto {
    @NotBlank(message = "Account name is required.")
    private String name;
}
