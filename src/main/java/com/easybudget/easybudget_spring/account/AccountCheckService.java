package com.easybudget.easybudget_spring.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.exception.NotFoundException;

@Transactional
@Service
public class AccountCheckService {

    @Autowired
    private AccountRepository accountRepository;

    public Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + id));
    }
}
