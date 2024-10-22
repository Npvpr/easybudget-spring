package com.easybudget.easybudget_spring.entry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.EasybudgetSpringApplication;
import com.easybudget.easybudget_spring.account.Account;
import com.easybudget.easybudget_spring.account.AccountBalanceService;
import com.easybudget.easybudget_spring.account.AccountCheckService;
import com.easybudget.easybudget_spring.category.CategoryCheckService;

// @Transactional is an annotation in Spring that helps manage database transactions. 
// When you do multiple actions in a database (like save, update, or delete), 
// you want all the actions to succeed or none at all (to avoid incomplete data). 
// @Transactional ensures that if something goes wrong (like an error), 
// all the changes are undone automatically, so your database stays consistent.
@Transactional
@Service
public class EntryService {

    private static final Logger log = LoggerFactory.getLogger(EasybudgetSpringApplication.class);

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

    public List<Entry> getAllEntries() {
        return entryRepository.findAll();
    }

    public Entry getEntryById(Long id) {
        return entryCheckService.findEntryById(id);
    }

    public Entry addEntry(Entry entry) {
        Account connectedAccount = accountCheckService.findAccountById(entry.getAccount().getId());
        accountBalanceService.updateAccountBalanceOfNewEntry(entry, connectedAccount);

        categoryCheckService.findCategoryById(entry.getCategory().getId());

        // Wrong Account's name will not be saved because only account_id is checked and
        // recorded together, but wrong account's name will be returned back here
        entryRepository.save(entry);

        // This is not essential, I just want to return an Entry with updated Account's
        // balance
        // This method can also fix Entries entering with wrong Account/Category
        // datas(Rigth IDs)
        // By returning correct datas
        entry.setAccount(accountCheckService.findAccountById(entry.getAccount().getId()));
        return entry;
    }

    public Entry updateEntry(Long id, Entry entry) {
        // if cost changed, update account again
        Entry existingEntry = entryCheckService.findEntryById(id);
        Account newAccount = accountCheckService.findAccountById(entry.getAccount().getId());
        categoryCheckService.findCategoryById(entry.getCategory().getId());

        accountBalanceService.updateAccountBalanceOfOldEntry(existingEntry, entry, newAccount);

        existingEntry.setType(entry.getType());
        existingEntry.setAccount(entry.getAccount());
        existingEntry.setCategory(entry.getCategory());
        existingEntry.setCost(entry.getCost());
        existingEntry.setDateTime(entry.getDateTime());
        existingEntry.setDescription(entry.getDescription());
        return entryRepository.save(existingEntry);
    }

    public void deleteEntry(Long id) {
        Entry entry = entryCheckService.findEntryById(id);
        accountBalanceService.updateAccountBalanceOfDeletedEntry(entry);

        entryRepository.deleteById(id);
    }

    public void deleteAllEntriesByAccountId(Long id) {
        // related account will be deleted so account balance
        // will not be required to update
        entryRepository.deleteByAccountId(id);
    }

    public void deleteAllEntriesByCategoryId(Long id) {
        // update all accounts related to all entries
        // related to the deleted category
        Specification<Entry> spec = Specification.where(EntrySpecificatioin.hasCategoryById(id));
        List<Entry> entries = entryRepository.findAll(spec);
        accountBalanceService.updateAccountBalanceOfDeletedCategory(entries);

        entryRepository.deleteByCategoryId(id);
    }

    public Map<String, Object> filterEntriesForMonthEntry(int year, int month) {

        LocalDateTime startDateTime = LocalDateTime.of(
                year, month, 1, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(
                year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59, 59);

        Specification<Entry> spec = Specification
                .where(EntrySpecificatioin.hasDateTimeBetween(startDateTime, endDateTime));

        List<Entry> entries = entryRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "dateTime"));

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalOutcome = BigDecimal.ZERO;
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (Entry entry : entries) {
            if (entry.getType() == Type.INCOME) {
                totalIncome = totalIncome.add(entry.getCost());
                totalBalance = totalBalance.add(entry.getCost());
            } else {
                totalOutcome = totalOutcome.add(entry.getCost());
                totalBalance = totalBalance.subtract(entry.getCost());
            }
        }

        Map<String, Object> result = new HashMap<>();

        result.put("entries", entries);
        result.put("totalIncome", totalIncome);
        result.put("totalOutcome", totalOutcome);
        result.put("totalBalance", totalBalance);

        return result;
    }

    private Map<String, Object> filterEntriesForGraphs(Integer year, Integer month, Integer endYear) {

        LocalDateTime startDateTime, endDateTime;

        if (month != null) {
            startDateTime = LocalDateTime.of(
                    year, month, 1, 0, 0, 0);
            endDateTime = LocalDateTime.of(
                    year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59, 59);
        } else if (endYear != null) {
            startDateTime = LocalDateTime.of(
                    year, 1, 1, 0, 0, 0);
            endDateTime = LocalDateTime.of(
                    endYear, 12, 31, 23, 59, 59);
        } else {
            startDateTime = LocalDateTime.of(
                    year, 1, 1, 0, 0, 0);
            endDateTime = LocalDateTime.of(
                    year, 12, 31, 23, 59, 59);
        }

        Specification<Entry> spec = Specification
                .where(EntrySpecificatioin.hasDateTimeBetween(startDateTime, endDateTime));

        List<Entry> entries = entryRepository.findAll(spec);

        Map<Object, BigDecimal> incomeList = new HashMap<>();
        Map<String, BigDecimal> incomeCategoryCostList = new HashMap<>();
        Map<String, BigDecimal> incomeCategoryPercentageList = new HashMap<>();
        Map<Object, BigDecimal> outcomeList = new HashMap<>();
        Map<String, BigDecimal> outcomeCategoryCostList = new HashMap<>();
        Map<String, BigDecimal> outcomeCategoryPercentageList = new HashMap<>();

        for (Entry entry : entries) {
            Object date = (month != null) ? entry.getDateTime().getDayOfMonth()
                    : (endYear != null) ? entry.getDateTime().getYear() : entry.getDateTime().getMonth();
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
            LocalDateTime startDate,
            LocalDateTime endDate, String sortField, String sortOrder) {

        Specification<Entry> spec = Specification
                .where(EntrySpecificatioin.hasType(type))
                .and(EntrySpecificatioin.hasAccountById(accountId))
                .and(EntrySpecificatioin.hasCategoryById(categoryId))
                .and(EntrySpecificatioin.hasDateTimeBetween(startDate, endDate));
        ;

        // This makes DateTime Descending Order as default, even if frontend requests
        // false/null inputs
        sortField = "cost".equalsIgnoreCase(sortField) ? "cost" : "dateTime";
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(sortDirection, sortField);

        List<Entry> entries = entryRepository.findAll(spec, sort);
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Entry entry : entries) {
            if (entry.getType() == Type.INCOME) {
                totalCost = totalCost.add(entry.getCost());
            } else {
                totalCost = totalCost.subtract(entry.getCost());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("entries", entries);
        result.put("totalCost", totalCost);

        return result;
    }
}
