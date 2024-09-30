package com.easybudget.easybudget_spring.account;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "AccountName cannot be null.")
    @Size(max = 100, message = "AccountName cannot exceed 100 characters.")
    private String name;

    @NotNull(message = "Balance cannot be null.")
    // include means 1 billion is included
    @DecimalMin(value = "-1000000000.00", inclusive = true, message = "Balance cannot be lower than negative 1 billion(debt)")
    @DecimalMax(value = "1000000000.00", inclusive = true, message = "Balance cannot be greater than 1 billion.(Congratulations if this error occured, LOL!)")
    private BigDecimal balance;

    public Account() {

    }

    public Account(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    // public void setId(Long id) {
    // this.id = id;
    // }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account [id=" + id + ", name=" + name + ", balance=" + balance + "]";
    }

}
