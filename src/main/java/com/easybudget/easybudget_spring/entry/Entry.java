package com.easybudget.easybudget_spring.entry;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.easybudget.easybudget_spring.account.Account;
import com.easybudget.easybudget_spring.category.Category;
import com.easybudget.easybudget_spring.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entry {
    @Id
    // GenerationType.AUTO skipped so many numbers for some reason
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    // This will save type column as strings
    // Without this, default EnumType is Ordinal, which will save as IDs
    // Changing this caused database error because entry_check for IDs(integers)
    // still existed(removed it)
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // After deleting all categories, I added new entry and got "detached entity
    // passed to persist" error => Removing cascade fixed the error
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // BigDecimal is the best data type for currency due to "GOOGLE"
    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    // SQL is not case sensitive, so you can't name dateTime
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Override
    public String toString() {

        return String.format(
                "[%s] %s: €%.2f via %s (%s) | %s",
                date,
                category.getName(),
                cost,
                account.getName(),
                type,
                description);
    }

}
