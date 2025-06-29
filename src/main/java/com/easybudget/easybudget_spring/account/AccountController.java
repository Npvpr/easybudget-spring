package com.easybudget.easybudget_spring.account;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/all")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return new ResponseEntity<>(accountService.getAllAccounts(), HttpStatus.OK);
    }

    @GetMapping("/totalBalance")
    public ResponseEntity<BigDecimal> getTotalBalance() {
        return new ResponseEntity<>(accountService.getTotalBalance(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<AccountDto> getAccountById(@RequestParam Long accountId) {
        return new ResponseEntity<>(accountService.getAccountById(accountId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AccountDto> addAccount(@RequestParam String accountName) {
        return new ResponseEntity<>(accountService.addAccount(accountName), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<AccountDto> updateAccount(@RequestParam Long accountId, @RequestParam String newAccountName) {
        return new ResponseEntity<>(accountService.updateAccount(accountId, newAccountName), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@RequestParam Long accountId) {
        accountService.deleteAccount(accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
