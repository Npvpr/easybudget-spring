package com.easybudget.easybudget_spring.account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easybudget.easybudget_spring.user.User;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByUser(User user);
}
