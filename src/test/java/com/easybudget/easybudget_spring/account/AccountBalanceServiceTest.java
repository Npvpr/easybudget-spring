package com.easybudget.easybudget_spring.account;

import com.easybudget.easybudget_spring.entry.Entry;
import com.easybudget.easybudget_spring.entry.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountBalanceServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private AccountCheckService accountCheckService;

  @InjectMocks
  private AccountBalanceService accountBalanceService;

  private Account account;
  private Entry entry;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    account = new Account();
    account.setId(1L);
    account.setBalance(BigDecimal.valueOf(100));
  }

  @Test
  void updateAccountBalanceOfNewEntry_income_shouldIncreaseBalance() {
    entry = new Entry();
    entry.setType(Type.INCOME);
    entry.setCost(BigDecimal.valueOf(50));

    when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

    Account updated = accountBalanceService.updateAccountBalanceOfNewEntry(entry, account);

    assertEquals(BigDecimal.valueOf(150), updated.getBalance());
  }

  @Test
  void updateAccountBalanceOfNewEntry_outcome_shouldDecreaseBalance() {
    entry = new Entry();
    entry.setType(Type.OUTCOME);
    entry.setCost(BigDecimal.valueOf(30));

    when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

    Account updated = accountBalanceService.updateAccountBalanceOfNewEntry(entry, account);

    assertEquals(BigDecimal.valueOf(70), updated.getBalance());
  }

  @Test
  void updateAccountBalanceOfOldEntry_editCost_shouldRecalculateBalances() {
    Entry oldEntry = new Entry();
    oldEntry.setAccount(account);
    oldEntry.setType(Type.INCOME);
    oldEntry.setCost(BigDecimal.valueOf(40));

    Entry newEntry = new Entry();
    newEntry.setAccount(account);
    newEntry.setType(Type.INCOME);
    newEntry.setCost(BigDecimal.valueOf(60));

    when(accountCheckService.findAccountById(account.getId())).thenReturn(account);
    when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

    Account updated = accountBalanceService.updateAccountBalanceOfOldEntry(oldEntry, newEntry, account);

    // First subtract old 40, then add new 60 → net +20
    assertEquals(BigDecimal.valueOf(120), updated.getBalance());
  }

  @Test
  void updateAccountBalanceOfDeletedEntry_income_shouldDecreaseBalance() {
    entry = new Entry();
    entry.setAccount(account);
    entry.setType(Type.INCOME);
    entry.setCost(BigDecimal.valueOf(50));

    when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

    Account updated = accountBalanceService.updateAccountBalanceOfDeletedEntry(entry);

    assertEquals(BigDecimal.valueOf(50), updated.getBalance());
  }

  @Test
  void updateAccountBalanceOfDeletedEntry_outcome_shouldIncreaseBalance() {
    entry = new Entry();
    entry.setAccount(account);
    entry.setType(Type.OUTCOME);
    entry.setCost(BigDecimal.valueOf(20));

    when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

    Account updated = accountBalanceService.updateAccountBalanceOfDeletedEntry(entry);

    assertEquals(BigDecimal.valueOf(120), updated.getBalance());
  }
}
