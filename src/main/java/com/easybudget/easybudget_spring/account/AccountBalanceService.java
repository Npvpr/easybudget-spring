package com.easybudget.easybudget_spring.account;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.entry.Entry;
import com.easybudget.easybudget_spring.entry.Type;

@Transactional
@Service
public class AccountBalanceService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountCheckService accountCheckService;

    public Account updateAccountBalanceOfNewEntry(Entry entry, Account account) {
        // Account existence is already checked in Entry
        // But account should not be connected from entry.getAccount()
        // Because only account_id is guaranteed to be correct
        // account_name and account_balance could be incorrect

        if (entry.getType() == Type.INCOME) {
            account.setBalance(account.getBalance().add(entry.getCost()));
        } else {
            account.setBalance(account.getBalance().subtract(entry.getCost()));
        }
        return accountRepository.save(account);
    }

    public Account updateAccountBalanceOfOldEntry(Entry oldEntry, Entry newEntry, Account newAccount) {
        Account oldAccount = accountCheckService.findAccountById(oldEntry.getAccount().getId());
        Type oldType = oldEntry.getType();
        Type newType = newEntry.getType();
        BigDecimal oldCost = oldEntry.getCost();
        BigDecimal newCost = newEntry.getCost();

        // If one of them is edited, just cancel the old entry and record new one
        if ((oldAccount.getId() != newAccount.getId()) || (oldType != newType) || (oldCost.compareTo(newCost) != 0)) {
            if (oldType == Type.INCOME) {
                oldAccount.setBalance(oldAccount.getBalance().subtract(oldCost));
            } else {
                oldAccount.setBalance(oldAccount.getBalance().add(oldCost));
            }

            if (newType == Type.INCOME) {
                newAccount.setBalance(newAccount.getBalance().add(newCost));
            } else {
                newAccount.setBalance(newAccount.getBalance().subtract(newCost));
            }
        }

        return accountRepository.save(newAccount);
    }

    public Account updateAccountBalanceOfDeletedEntry(Entry entry) {
        Account account = entry.getAccount();

        if (entry.getType() == Type.INCOME) {
            account.setBalance(account.getBalance().subtract(entry.getCost()));
        } else {
            account.setBalance(account.getBalance().add(entry.getCost()));
        }
        return accountRepository.save(account);
    }

    public void updateAccountBalanceOfDeletedCategory(List<Entry> entries) {
        for (Entry entry : entries) {
            this.updateAccountBalanceOfDeletedEntry(entry);
        }
    }
}
