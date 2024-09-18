package com.easybudget.easybudget_spring.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.entry.EntryService;
import com.easybudget.easybudget_spring.exception.NotFoundException;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    public EntryService entryService;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not with id: " + id));
    }

    public Account addAccount(Account Account) {
        return accountRepository.save(Account);
    }

    public Account updateAccount(Long id, Account Account) {
        Account existingAccount = this.getAccountById(id);
        if (existingAccount != null) {
            existingAccount.setName(Account.getName());
            return accountRepository.save(existingAccount);
        } else {
            // this doesn't need to return exception because get by id will run NotFound
            // exception if id doesn't exist
            return null;
        }
    }

    public void deleteAccount(Long id) {
        entryService.deleteAllEntriesByAccountId(id);
        accountRepository.deleteById(id);
    }
}
