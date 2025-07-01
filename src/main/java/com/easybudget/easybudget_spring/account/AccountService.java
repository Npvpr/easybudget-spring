package com.easybudget.easybudget_spring.account;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.entry.EntryService;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;

@Transactional
@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountCheckService accountCheckService;

    @Autowired
    public EntryService entryService;

    @Autowired
    private UserService userService;

    public List<AccountDto> getAllAccounts() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<Account> accounts = accountRepository.findAllByUser(currentUser);
        return accounts.stream()
                .map(AccountMapper::toDto)
                .toList();
    }

    public BigDecimal getTotalBalance() {
        List<AccountDto> accountDtos = getAllAccounts();
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (AccountDto accountDto : accountDtos) {
            totalBalance = totalBalance.add(accountDto.getBalance());
        }
        return totalBalance;
    }

    public AccountDto getAccountById(Long accountId) {
        Account account = accountCheckService.findAccountById(accountId);
        return AccountMapper.toDto(account);
    }

    public AccountDto addAccount(CreateAccountRequestDto accountRequest) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Account newAccount = Account.builder()
                .name(accountRequest.getName())
                // New Account should start with 0 balance!
                .balance(BigDecimal.ZERO)
                .user(currentUser)
                .build();

        accountRepository.save(newAccount);

        return AccountMapper.toDto(newAccount);
    }

    public AccountDto updateAccount(Long accountId, UpdateAccountRequestDto accountRequest) {
        Account existingAccount = accountCheckService.findAccountById(accountId);

        existingAccount.setName(accountRequest.getName());
        // Don't let users manually edit Account's Balance
        // Always update Account's balance from Entry
        // existingAccount.setBalance(Account.getBalance());
        accountRepository.save(existingAccount);

        return AccountMapper.toDto(existingAccount);
    }

    public String deleteAccount(Long accountId) {
        accountCheckService.findAccountById(accountId);

        entryService.deleteAllEntriesByAccountId(accountId);
        accountRepository.deleteById(accountId);

        return "Account with ID " + accountId + " has been deleted successfully.";
    }
}
