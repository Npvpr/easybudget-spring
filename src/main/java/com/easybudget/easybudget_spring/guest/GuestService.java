package com.easybudget.easybudget_spring.guest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.account.AccountDto;
import com.easybudget.easybudget_spring.account.AccountService;
import com.easybudget.easybudget_spring.account.CreateAccountRequestDto;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;

@Service
public class GuestService {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    public void resetDb() {
        // This database reset is dangerous for other users
        User guestUser = userService.getCurrentAuthenticatedUser();
        if (!guestUser.getEmail().equals("guest@guest.guest")) {
            throw new IllegalStateException("This method should only be called by the guest user.");
        }

        // Let the guest user do anything they want while using the app (All CRUD)
        // But clean up and reset their data every login

        // Clean up

        List<AccountDto> accountDtos = accountService.getAllAccounts();

        for (AccountDto accountDto : accountDtos) {
            // Delete all accounts of the guest user
            accountService.deleteAccount(accountDto.getId());
        }

        // Repopulate

        accountService.addAccount(new CreateAccountRequestDto("Visa Card"));
        accountService.addAccount(new CreateAccountRequestDto("Master Card"));
        accountService.addAccount(new CreateAccountRequestDto("Cash"));

        // Whenever a guest user logs in, populate their data with current month's data
        // so that they can immediately start testing all the features
        
        // get current month
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        int currentYear = java.time.LocalDate.now().getYear();
    }
}
