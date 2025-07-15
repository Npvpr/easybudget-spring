package com.easybudget.easybudget_spring.entry;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.easybudget.easybudget_spring.account.AccountDto;
import com.easybudget.easybudget_spring.category.CategoryDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EntryDto {
    private Long id;
    private Type type;
    private AccountDto accountDto;
    private CategoryDto categoryDto;
    private BigDecimal cost;
    private LocalDate date;
    private String description;
}
