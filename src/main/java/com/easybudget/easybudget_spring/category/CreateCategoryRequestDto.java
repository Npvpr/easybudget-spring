package com.easybudget.easybudget_spring.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequestDto {
    @NotBlank(message = "Category name is required.")
    private String name;
}
