package com.easybudget.easybudget_spring.account;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountDto {
    private Long id;
    private String name;
    private BigDecimal balance;
}
