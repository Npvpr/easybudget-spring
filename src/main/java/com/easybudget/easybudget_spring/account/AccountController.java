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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return new ResponseEntity<>(accountService.getAllAccounts(), HttpStatus.OK);
    }

    @GetMapping("/balance/total")
    public ResponseEntity<BigDecimal> getTotalBalance() {
        return new ResponseEntity<>(accountService.getTotalBalance(), HttpStatus.OK);
    }

    // RequestParam is for optional metadata/ filtering/ modifiers
    // PathVariable is for specific resource
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long accountId) {
        return new ResponseEntity<>(accountService.getAccountById(accountId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AccountDto> addAccount(@RequestBody CreateAccountRequestDto accountRequest) {
        return new ResponseEntity<>(accountService.addAccount(accountRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long accountId,
            @RequestBody UpdateAccountRequestDto accountRequest) {
        return new ResponseEntity<>(accountService.updateAccount(accountId, accountRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountId) {
        return new ResponseEntity<>(accountService.deleteAccount(accountId), HttpStatus.OK);
    }

}
