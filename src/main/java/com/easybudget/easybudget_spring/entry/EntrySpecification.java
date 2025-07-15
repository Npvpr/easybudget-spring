package com.easybudget.easybudget_spring.entry;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.easybudget.easybudget_spring.user.User;

public class EntrySpecification {

    public static Specification<Entry> belongsToUser(User user) {
        return (root, query, criteriaBuilder) -> user == null ? null
                : criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Entry> hasType(Type type) {
        return (root, query, criteriaBuilder) -> type == null ? null
                : criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Entry> hasAccountById(Long accountId) {
        return (root, query, criteriaBuilder) -> accountId == null ? null
                : criteriaBuilder.equal(root.get("account").get("id"), accountId);
    }

    public static Specification<Entry> hasCategoryById(Long categoryId) {
        return (root, query, criteriaBuilder) -> categoryId == null ? null
                : criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Entry> hasDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null)
                return null;
            if (startDate != null && endDate != null)
                return criteriaBuilder.between(root.get("date"), startDate, endDate);
            if (startDate != null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
            return criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
        };
    }
}
