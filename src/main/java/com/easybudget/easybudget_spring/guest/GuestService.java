package com.easybudget.easybudget_spring.guest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.account.AccountDto;
import com.easybudget.easybudget_spring.account.AccountService;
import com.easybudget.easybudget_spring.account.CreateAccountRequestDto;
import com.easybudget.easybudget_spring.category.CategoryDto;
import com.easybudget.easybudget_spring.category.CategoryService;
import com.easybudget.easybudget_spring.category.CreateCategoryRequestDto;
import com.easybudget.easybudget_spring.entry.CreateEntryRequestDto;
import com.easybudget.easybudget_spring.entry.EntryService;
import com.easybudget.easybudget_spring.entry.Type;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;

@Service
public class GuestService {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EntryService entryService;

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

        List<CategoryDto> categoryDtos = categoryService.getAllCategories();

        for (CategoryDto categoryDto : categoryDtos) {
            // Delete all categories of the guest user
            categoryService.deleteCategory(categoryDto.getId());
        }

        // Whenever a guest user logs in, populate their data with current month's data
        // so that they can immediately start testing all the features
        // Repopulate

        accountService.createAccount(new CreateAccountRequestDto("Visa Card"));
        accountService.createAccount(new CreateAccountRequestDto("Master Card"));
        accountService.createAccount(new CreateAccountRequestDto("Cash"));

        categoryService.createCategory(new CreateCategoryRequestDto("Salary"));
        categoryService.createCategory(new CreateCategoryRequestDto("Deposit"));
        categoryService.createCategory(new CreateCategoryRequestDto("Food"));
        categoryService.createCategory(new CreateCategoryRequestDto("Transport"));
        categoryService.createCategory(new CreateCategoryRequestDto("Entertainment"));

        // get current month
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        int currentYear = java.time.LocalDate.now().getYear();

        entryService.createEntry(new CreateEntryRequestDto(Type.INCOME, 1L, 1L, BigDecimal.valueOf(3000), LocalDateTime.of(currentYear, currentMonth, 1, 0, 0), "Salary for the month"));
        entryService.createEntry(new CreateEntryRequestDto(Type.INCOME, 2L, 2L, BigDecimal.valueOf(500), LocalDateTime.of(currentYear, currentMonth, 1, 0, 0), "Deposit to Master Card account"));
        entryService.createEntry(new CreateEntryRequestDto(Type.INCOME, 3L, 2L, BigDecimal.valueOf(600), LocalDateTime.of(currentYear, currentMonth, 1, 0, 0), "Cash deposit"));
        entryService.createEntry(new CreateEntryRequestDto(Type.OUTCOME, 1L, 3L, BigDecimal.valueOf(50), LocalDateTime.of(currentYear, currentMonth, 2, 0, 0), "Food expenses"));
        entryService.createEntry(new CreateEntryRequestDto(Type.OUTCOME, 1L, 4L, BigDecimal.valueOf(20), LocalDateTime.of(currentYear, currentMonth, 3, 0, 0), "Transport expenses"));
        entryService.createEntry(new CreateEntryRequestDto(Type.OUTCOME, 1L, 5L, BigDecimal.valueOf(100), LocalDateTime.of(currentYear, currentMonth, 4, 0, 0), "Entertainment expenses"));
        entryService.createEntry(new CreateEntryRequestDto(Type.OUTCOME, 2L, 3L, BigDecimal.valueOf(60), LocalDateTime.of(currentYear, currentMonth, 5, 0, 0), "Food expenses on Master Card"));
        entryService.createEntry(new CreateEntryRequestDto(Type.OUTCOME, 3L, 4L, BigDecimal.valueOf(30), LocalDateTime.of(currentYear, currentMonth, 6, 0, 0), "Transport expenses with Cash"));
        entryService.createEntry(new CreateEntryRequestDto(Type.OUTCOME, 2L, 5L, BigDecimal.valueOf(150), LocalDateTime.of(currentYear, currentMonth, 7, 0, 0), "Entertainment expenses on Master Card"));
    }
}
