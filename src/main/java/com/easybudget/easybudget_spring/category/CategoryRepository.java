package com.easybudget.easybudget_spring.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easybudget.easybudget_spring.user.User;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUser(User user);
}
