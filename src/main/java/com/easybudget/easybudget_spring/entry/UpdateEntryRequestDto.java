package com.easybudget.easybudget_spring.entry;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEntryRequestDto {
    @NotNull(message = "Type cannot be null.")
    private Type type;

    @NotNull(message = "Account ID cannot be null.")
    private Long accountId;

    @NotNull(message = "Category ID cannot be null.")
    private Long categoryId;

    @NotNull(message = "Cost cannot be null.")
    @DecimalMin(value = "0.01", inclusive = true, message = "Cost must be greater than zero.")
    @DecimalMax(value = "1000000000.00", inclusive = true, message = "Cost cannot exceed 1 billion.")
    private BigDecimal cost;

    @NotNull(message = "Date cannot be null.")
    private LocalDate date;

    @NotBlank(message = "Description is required.")
    @Size(min=5, max = 1000, message = "Description must be between 5 and 1000 characters.")
    private String description;
}
