package com.easybudget.easybudget_spring.entry;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.easybudget.easybudget_spring.category.Category;

import jakarta.persistence.CascadeType;
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

    @NotNull(message = "Type cannot be null.")
    // This will save type column as strings
    // Without this, default EnumType is Ordinal, which will save as IDs
    // Changing this caused database error because entry_check for IDs(integers)
    // still existed(removed it)
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull(message = "Category cannot be null.")
    // With cascade all, all persistance operations(CRUD) on the parent
    // entity(Category) will be automatically applied to the child entity(Entry)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull(message = "Cost cannot be null.")
    @DecimalMin(value = "0.01", inclusive = true, message = "Cost must be greater than zero.")
    @DecimalMax(value = "1000000000.00", inclusive = true, message = "Cost cannot exceed 1 billion.")
    // BigDecimal is the best data type for currency due to "GOOGLE"
    private BigDecimal cost;

    @NotNull(message = "DateTime cannot be null.")
    private LocalDateTime dateTime;

    @NotNull(message = "Description cannot be null.")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters.")
    private String description;

    public Entry() {

    }

    public Entry(Type type, Category category, BigDecimal cost, LocalDateTime dateTime, String description) {
        this.type = type;
        this.category = category;
        this.cost = cost;
        this.dateTime = dateTime;
        this.description = description;
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
