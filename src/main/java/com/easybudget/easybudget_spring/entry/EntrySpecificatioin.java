package com.easybudget.easybudget_spring.entry;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

public class EntrySpecificatioin {

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

    public static Specification<Entry> hasDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return (root, query, criteriaBuilder) -> {
            if (startDateTime == null && endDateTime == null)
                return null;
            if (startDateTime != null && endDateTime != null)
                return criteriaBuilder.between(root.get("dateTime"), startDateTime, endDateTime);
            if (startDateTime != null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("dateTime"), startDateTime);
            return criteriaBuilder.lessThanOrEqualTo(root.get("dateTime"), endDateTime);
        };
    }
}
