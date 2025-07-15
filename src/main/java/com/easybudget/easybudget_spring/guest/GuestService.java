package com.easybudget.easybudget_spring.guest;

import java.math.BigDecimal;
import java.time.LocalDate;
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

                AccountDto visaAccount = accountService.createAccount(new CreateAccountRequestDto("Visa Card"));
                AccountDto masterCardAccount = accountService.createAccount(new CreateAccountRequestDto("Master Card"));
                AccountDto cashAccount = accountService.createAccount(new CreateAccountRequestDto("Cash"));

                CategoryDto salaryCategory = categoryService.createCategory(new CreateCategoryRequestDto("Salary"));
                CategoryDto depositCategory = categoryService.createCategory(new CreateCategoryRequestDto("Deposit"));
                CategoryDto foodCategory = categoryService.createCategory(new CreateCategoryRequestDto("Food"));
                CategoryDto transportCategory = categoryService
                                .createCategory(new CreateCategoryRequestDto("Transport"));
                CategoryDto entertainmentCategory = categoryService
                                .createCategory(new CreateCategoryRequestDto("Entertainment"));

                // get current month
                int currentMonth = java.time.LocalDate.now().getMonthValue();
                int currentYear = java.time.LocalDate.now().getYear();

                entryService.createEntry(
                                new CreateEntryRequestDto(Type.INCOME, visaAccount.getId(), salaryCategory.getId(),
                                                BigDecimal.valueOf(3000), LocalDate.of(currentYear, currentMonth, 1),
                                                "Salary for the month"));
                entryService.createEntry(new CreateEntryRequestDto(Type.INCOME, masterCardAccount.getId(),
                                depositCategory.getId(), BigDecimal.valueOf(500),
                                LocalDate.of(currentYear, currentMonth, 2),
                                "Deposit to Master Card account"));
                entryService.createEntry(new CreateEntryRequestDto(Type.INCOME, cashAccount.getId(),
                                depositCategory.getId(),
                                BigDecimal.valueOf(600), LocalDate.of(currentYear, currentMonth, 3), "Cash deposit"));
                entryService.createEntry(new CreateEntryRequestDto(Type.OUTCOME, visaAccount.getId(),
                                foodCategory.getId(),
                                BigDecimal.valueOf(50), LocalDate.of(currentYear, currentMonth, 4), "Food expenses"));
                entryService.createEntry(
                                new CreateEntryRequestDto(Type.OUTCOME, visaAccount.getId(), transportCategory.getId(),
                                                BigDecimal.valueOf(20), LocalDate.of(currentYear, currentMonth, 5),
                                                "Transport expenses"));
                // entryService.createEntry(new CreateEntryRequestDto(Type.OUTCOME, visaAccount.getId(),
                //                 entertainmentCategory.getId(), BigDecimal.valueOf(100),
                //                 LocalDate.of(currentYear, currentMonth, 6), "Entertainment expenses"));
                // entryService.createEntry(new CreateEntryRequestDto(Type.OUTCOME, masterCardAccount.getId(),
                //                 foodCategory.getId(), BigDecimal.valueOf(60),
                //                 LocalDate.of(currentYear, currentMonth, 7),
                //                 "Food expenses on Master Card"));
                // entryService.createEntry(
                //                 new CreateEntryRequestDto(Type.OUTCOME, cashAccount.getId(), transportCategory.getId(),
                //                                 BigDecimal.valueOf(30), LocalDate.of(currentYear, currentMonth, 8),
                //                                 "Transport expenses with Cash"));
                // entryService.createEntry(new CreateEntryRequestDto(Type.OUTCOME, masterCardAccount.getId(),
                //                 entertainmentCategory.getId(), BigDecimal.valueOf(150),
                //                 LocalDate.of(currentYear, currentMonth, 9), "Entertainment expenses on Master Card"));
        }
}
