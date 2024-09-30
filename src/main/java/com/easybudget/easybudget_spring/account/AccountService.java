package com.easybudget.easybudget_spring.account;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.entry.EntryService;

@Transactional
@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountCheckService accountCheckService;

    @Autowired
    public EntryService entryService;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public BigDecimal getTotalBalance() {
        List<Account> accounts = accountRepository.findAll();
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (Account account : accounts) {
            totalBalance = totalBalance.add(account.getBalance());
        }
        return totalBalance;
    }

    public Account getAccountById(Long id) {
        return accountCheckService.findAccountById(id);
    }

    public Account addAccount(Account account) {
        // New Account should start with 0 balance!
        account.setBalance(BigDecimal.ZERO);

        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account account) {
        Account existingAccount = accountCheckService.findAccountById(id);

        existingAccount.setName(account.getName());
        // Don't let users manually edit Account's Balance
        // Always update Account's balance from Entry
        // existingAccount.setBalance(Account.getBalance());
        return accountRepository.save(existingAccount);
    }

    public void deleteAccount(Long id) {
        accountCheckService.findAccountById(id);

        entryService.deleteAllEntriesByAccountId(id);
        accountRepository.deleteById(id);
    }
}
