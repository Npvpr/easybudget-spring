package com.easybudget.easybudget_spring.entry;

import com.easybudget.easybudget_spring.account.AccountMapper;
import com.easybudget.easybudget_spring.category.CategoryMapper;

public class EntryMapper {
    public static EntryDto toDto(Entry entry) {
        if (entry == null) {
            return null;
        }
        return EntryDto.builder()
                .id(entry.getId())
                .type(entry.getType())
                .accountDto(AccountMapper.toDto(entry.getAccount()))
                .categoryDto(CategoryMapper.toDto(entry.getCategory()))
                .cost(entry.getCost())
                .date(entry.getDate())
                .description(entry.getDescription())
                .build();
    }
}
