package com.easybudget.easybudget_spring.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public Account() {

    }

    public Account(String name) {
        this.name = name;
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

}
