package com.easybudget.easybudget_spring.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.exception.NotFoundException;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;

@Transactional
@Service
public class AccountCheckService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;

    public Account findAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + id));

        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser != account.getUser()) {
            throw new NotFoundException("Account not found for the current user");
        }
        return account;
    }
}
