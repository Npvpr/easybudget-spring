package com.easybudget.easybudget_spring.entry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.account.Account;
import com.easybudget.easybudget_spring.account.AccountBalanceService;
import com.easybudget.easybudget_spring.account.AccountCheckService;
import com.easybudget.easybudget_spring.category.Category;
import com.easybudget.easybudget_spring.category.CategoryCheckService;
import com.easybudget.easybudget_spring.embedding.EmbeddingService;
import com.easybudget.easybudget_spring.embedding.EntryEmbedding;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;

// @Transactional is an annotation in Spring that helps manage database transactions. 
// When you do multiple actions in a database (like save, update, or delete), 
// you want all the actions to succeed or none at all (to avoid incomplete data). 
// @Transactional ensures that if something goes wrong (like an error), 
// all the changes are undone automatically, so your database stays consistent.
@Transactional
@Service
public class EntryService {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private EntryCheckService entryCheckService;

    @Autowired
    private AccountCheckService accountCheckService;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private CategoryCheckService categoryCheckService;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private UserService userService;

    public List<EntryDto> getAllEntries() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<Entry> entries = entryRepository.findAllByUser(currentUser);

        return entries.stream()
                .map(EntryMapper::toDto)
                .toList();
    }

    public EntryDto getEntryById(Long entryId) {
        Entry entry = entryCheckService.findEntryById(entryId);
        System.out.println(entry.toString());
        return EntryMapper.toDto(entry);
    }

    public EntryDto createEntry(CreateEntryRequestDto createEntryRequestDto) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        // check if the account and category exist
        Account connectedAccount = accountCheckService.findAccountById(createEntryRequestDto.getAccountId());
        Category connectedCategory = categoryCheckService.findCategoryById(createEntryRequestDto.getCategoryId());

        // update the connected account with the new entry's cost
        Entry newEntry = Entry.builder()
                .type(createEntryRequestDto.getType())
                .user(currentUser)
                .account(connectedAccount)
                .category(connectedCategory)
                .cost(createEntryRequestDto.getCost())
                .date(createEntryRequestDto.getDate())
                .description(createEntryRequestDto.getDescription())
                .build();
        accountBalanceService.updateAccountBalanceOfNewEntry(newEntry, connectedAccount);

        // try {
        // Thread.sleep(300);
        // } catch (InterruptedException e) {
        // Thread.currentThread().interrupt();
        // throw new RuntimeException("Interrupted during rate limit delay", e);
        // }

        embeddingService.createEntryEmbedding(newEntry);

        Entry savedEntry = entryRepository.save(newEntry);
        System.out.println("Saved Entry: " + savedEntry.toString());

        return EntryMapper.toDto(savedEntry);
    }

    public EntryDto updateEntry(Long entryId, UpdateEntryRequestDto updateEntryRequestDto) {
        // if cost changed, update account again
        Entry oldEntry = entryCheckService.findEntryById(entryId);

        // check if the account and category exist
        Account newAccount = accountCheckService.findAccountById(updateEntryRequestDto.getAccountId());
        Category newCategory = categoryCheckService.findCategoryById(updateEntryRequestDto.getCategoryId());

        // update the existing/old entry with the new values
        Entry newEntry = Entry.builder()
                .id(entryId)
                .type(updateEntryRequestDto.getType())
                .account(newAccount)
                .category(newCategory)
                .cost(updateEntryRequestDto.getCost())
                .date(updateEntryRequestDto.getDate())
                .description(updateEntryRequestDto.getDescription())
                .build();
        accountBalanceService.updateAccountBalanceOfOldEntry(oldEntry, newEntry, newAccount);

        oldEntry.setType(newEntry.getType());
        oldEntry.setAccount(newEntry.getAccount());
        oldEntry.setCategory(newEntry.getCategory());
        oldEntry.setCost(newEntry.getCost());
        oldEntry.setDate(newEntry.getDate());
        oldEntry.setDescription(newEntry.getDescription());

        embeddingService.updateEntryEmbedding(oldEntry);

        Entry savedEntry = entryRepository.save(oldEntry);

        return EntryMapper.toDto(savedEntry);
    }

    public String deleteEntry(Long entryId) {
        Entry deletingEntry = entryCheckService.findEntryById(entryId);
        accountBalanceService.updateAccountBalanceOfDeletedEntry(deletingEntry);

        embeddingService.deleteEntryEmbedding(entryId);
        entryRepository.deleteById(entryId);

        return "Entry with ID " + entryId + " deleted successfully.";
    }

    public String deleteAllEntriesByAccountId(Long accountId) {
        // related account will be deleted so account balance
        // will not be required to update
        Account deletingAccount = accountCheckService.findAccountById(accountId);
        String accountName = deletingAccount.getName();

        entryRepository.deleteByAccountId(accountId);

        return accountName + " Account's all entries deleted successfully.";
    }

    public String deleteAllEntriesByCategoryId(Long categoryId) {
        // update all accounts related to all entries
        // related to the deleted category
        Category deletingCategory = categoryCheckService.findCategoryById(categoryId);
        String categoryName = deletingCategory.getName();

        User currentUser = userService.getCurrentAuthenticatedUser();

        Specification<Entry> spec = Specification.where(EntrySpecification.hasCategoryById(categoryId))
                .and(EntrySpecification.belongsToUser(currentUser));
        List<Entry> entries = entryRepository.findAll(spec);
        accountBalanceService.updateAccountBalanceOfDeletedCategory(entries);

        entryRepository.deleteByCategoryId(categoryId);

        return categoryName + " Category's all entries deleted successfully.";
    }

    public Map<String, Object> filterEntriesForMonthEntry(int year, int month) {

        LocalDate startDate = LocalDate.of(
                year, month, 1);
        LocalDate endDate = LocalDate.of(
                year, month, YearMonth.of(year, month).lengthOfMonth());

        User currentUser = userService.getCurrentAuthenticatedUser();

        Specification<Entry> spec = Specification
                .where(EntrySpecification.hasDateBetween(startDate, endDate))
                .and(EntrySpecification.belongsToUser(currentUser));

        List<Entry> entries = entryRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "date"));

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalOutcome = BigDecimal.ZERO;
        BigDecimal totalBalance = BigDecimal.ZERO;

        List<EntryDto> entryDtos = new ArrayList<>();

        for (Entry entry : entries) {
            if (entry.getType() == Type.INCOME) {
                totalIncome = totalIncome.add(entry.getCost());
                totalBalance = totalBalance.add(entry.getCost());
            } else {
                totalOutcome = totalOutcome.add(entry.getCost());
                totalBalance = totalBalance.subtract(entry.getCost());
            }
            entryDtos.add(EntryMapper.toDto(entry));
        }

        Map<String, Object> result = new HashMap<>();

        result.put("entries", entryDtos);
        result.put("totalIncome", totalIncome);
        result.put("totalOutcome", totalOutcome);
        result.put("totalBalance", totalBalance);

        return result;
    }

    private Map<String, Object> filterEntriesForGraphs(Integer year, Integer month, Integer endYear) {

        LocalDate startDate, endDate;

        if (month != null) {
            startDate = LocalDate.of(
                    year, month, 1);
            endDate = LocalDate.of(
                    year, month, YearMonth.of(year, month).lengthOfMonth());
        } else if (endYear != null) {
            startDate = LocalDate.of(
                    year, 1, 1);
            endDate = LocalDate.of(
                    endYear, 12, 31);
        } else {
            startDate = LocalDate.of(
                    year, 1, 1);
            endDate = LocalDate.of(
                    year, 12, 31);
        }

        User currentUser = userService.getCurrentAuthenticatedUser();

        Specification<Entry> spec = Specification
                .where(EntrySpecification.hasDateBetween(startDate, endDate))
                .and(EntrySpecification.belongsToUser(currentUser));

        List<Entry> entries = entryRepository.findAll(spec);

        Map<Object, BigDecimal> incomeList = new HashMap<>();
        Map<String, BigDecimal> incomeCategoryCostList = new HashMap<>();
        Map<String, BigDecimal> incomeCategoryPercentageList = new HashMap<>();
        Map<Object, BigDecimal> outcomeList = new HashMap<>();
        Map<String, BigDecimal> outcomeCategoryCostList = new HashMap<>();
        Map<String, BigDecimal> outcomeCategoryPercentageList = new HashMap<>();

        for (Entry entry : entries) {
            Object date = (month != null) ? entry.getDate().getDayOfMonth()
                    : (endYear != null) ? entry.getDate().getYear() : entry.getDate().getMonth();
            String categoryName = entry.getCategory().getName();

            if (entry.getType() == Type.INCOME) {
                incomeList.put(date, incomeList.getOrDefault(date, BigDecimal.ZERO).add(entry.getCost()));
                incomeCategoryCostList.put(categoryName,
                        incomeCategoryCostList.getOrDefault(categoryName, BigDecimal.ZERO).add(entry.getCost()));
            } else {
                outcomeList.put(date, outcomeList.getOrDefault(date, BigDecimal.ZERO).add(entry.getCost()));
                outcomeCategoryCostList.put(categoryName,
                        outcomeCategoryCostList.getOrDefault(categoryName, BigDecimal.ZERO).add(entry.getCost()));
            }
        }

        incomeCategoryPercentageList = calculateCategoryPercentages(incomeCategoryCostList);
        outcomeCategoryPercentageList = calculateCategoryPercentages(outcomeCategoryCostList);

        Map<String, Object> result = new HashMap<>();

        result.put("incomeList", incomeList);
        result.put("incomeCategoryCostList", incomeCategoryCostList);
        result.put("incomeCategoryPercentageList", incomeCategoryPercentageList);
        result.put("outcomeList", outcomeList);
        result.put("outcomeCategoryCostList", outcomeCategoryCostList);
        result.put("outcomeCategoryPercentageList", outcomeCategoryPercentageList);

        return result;
    }

    private Map<String, BigDecimal> calculateCategoryPercentages(Map<String, BigDecimal> categoryCostList) {
        BigDecimal totalCategoryAmount = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryPercentageList = new HashMap<>();

        for (BigDecimal amount : categoryCostList.values()) {
            totalCategoryAmount = totalCategoryAmount.add(amount);
        }

        for (Map.Entry<String, BigDecimal> entry : categoryCostList.entrySet()) {
            String categoryName = entry.getKey(); // Get the key (category name)
            BigDecimal categoryAmount = entry.getValue(); // Get the value (category amount)

            categoryAmount = categoryAmount
                    .divide(totalCategoryAmount, 2, RoundingMode.HALF_UP) // Divide and specify scale and rounding mode
                    .multiply(new BigDecimal("100")); // Multiply by 100 to get the percentage

            categoryPercentageList.put(categoryName, categoryAmount);
        }

        return categoryPercentageList;
    }

    public Map<String, Object> filterEntriesForGraphsOfMonth(int year, int month) {
        return filterEntriesForGraphs(year, month, null);
    }

    public Map<String, Object> filterEntriesForGraphsOfYear(int year) {
        return filterEntriesForGraphs(year, null, null);
    }

    public Map<String, Object> filterEntriesForGraphsOfYears(int startYear, int endYear) {
        return filterEntriesForGraphs(startYear, null, endYear);
    }

    public Map<String, Object> filterEntriesForHistory(Type type, Long accountId, Long categoryId,
            LocalDate startDate,
            LocalDate endDate, String sortField, String sortOrder) {

        User currentUser = userService.getCurrentAuthenticatedUser();

        Specification<Entry> spec = Specification
                .where(EntrySpecification.hasType(type))
                .and(EntrySpecification.belongsToUser(currentUser))
                .and(EntrySpecification.hasAccountById(accountId))
                .and(EntrySpecification.hasCategoryById(categoryId))
                .and(EntrySpecification.hasDateBetween(startDate, endDate));
        ;

        // This makes Date Descending Order as default, even if frontend requests
        // false/null inputs
        sortField = "cost".equalsIgnoreCase(sortField) ? "cost" : "date";
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(sortDirection, sortField);

        List<Entry> entries = entryRepository.findAll(spec, sort);
        BigDecimal totalCost = BigDecimal.ZERO;

        List<EntryDto> entryDtos = new ArrayList<>();

        for (Entry entry : entries) {
            if (entry.getType() == Type.INCOME) {
                totalCost = totalCost.add(entry.getCost());
            } else {
                totalCost = totalCost.subtract(entry.getCost());
            }
            entryDtos.add(EntryMapper.toDto(entry));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("entries", entryDtos);
        result.put("totalCost", totalCost);

        return result;
    }
}
