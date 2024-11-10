package com.easybudget.easybudget_spring.entry;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.easybudget.easybudget_spring.account.Account;
import com.easybudget.easybudget_spring.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Entry {
    @Id
    // GenerationType.AUTO skipped so many numbers for some reason
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    @NotNull(message = "Type cannot be null.")
    // This will save type column as strings
    // Without this, default EnumType is Ordinal, which will save as IDs
    // Changing this caused database error because entry_check for IDs(integers)
    // still existed(removed it)
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull(message = "Account cannot be null")
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull(message = "Category cannot be null.")
    // After deleting all categories, I added new entry and got "detached entity
    // passed to persist" error => Removing cascade fixed the error
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull(message = "Cost cannot be null.")
    @DecimalMin(value = "0.01", inclusive = true, message = "Cost must be greater than zero.")
    @DecimalMax(value = "1000000000.00", inclusive = true, message = "Cost cannot exceed 1 billion.")
    // BigDecimal is the best data type for currency due to "GOOGLE"
    private BigDecimal cost;

    // SQL is not case sensitive, so you can't name dateTime
    @Column(name = "datetime")
    @NotNull(message = "DateTime cannot be null.")
    private LocalDateTime dateTime;

    @Column(name = "description")
    @NotNull(message = "Description cannot be null.")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters.")
    private String description;

    public Entry() {

    }

    public Entry(Type type, Account account, Category category, BigDecimal cost, LocalDateTime dateTime,
            String description) {
        this.type = type;
        this.account = account;
        this.category = category;
        this.cost = cost;
        this.dateTime = dateTime;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Entry [id=" + id + ", type=" + type + ", account=" + account + ", category=" + category + ", cost="
                + cost + ", dateTime=" + dateTime + ", description=" + description + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
