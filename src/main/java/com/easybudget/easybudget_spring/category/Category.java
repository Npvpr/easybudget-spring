package com.easybudget.easybudget_spring.category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easybudget.easybudget_spring.EasybudgetSpringApplication;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Category {

    private static final Logger log = LoggerFactory.getLogger(EasybudgetSpringApplication.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "CategoryName cannot be null.")
    @Size(max = 100, message = "CategoryName cannot exceed 100 characters.")
    private String name;

    public Category() {
        log.info("Category NoArgs~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public Category(String name) {
        log.info("Category AllArgs~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
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

    @Override
    public String toString() {
        return "Category{id=" + id + ", name=" + name + "}";
    }

}
