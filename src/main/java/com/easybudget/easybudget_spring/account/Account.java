package com.easybudget.easybudget_spring.account;

import java.math.BigDecimal;

import com.easybudget.easybudget_spring.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FetchType.LAZY is used to avoid loading the user unless needed
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "AccountName cannot be null.")
    @Size(max = 100, message = "AccountName cannot exceed 100 characters.")
    private String name;

    @NotNull(message = "Balance cannot be null.")
    // include means 1 billion is included
    @DecimalMin(value = "-1000000000.00", inclusive = true, message = "Balance cannot be lower than negative 1 billion(debt)")
    @DecimalMax(value = "1000000000.00", inclusive = true, message = "Balance cannot be greater than 1 billion.(Congratulations if this error occured, LOL!)")
    private BigDecimal balance;

}
