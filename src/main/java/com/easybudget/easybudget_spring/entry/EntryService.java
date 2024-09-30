package com.easybudget.easybudget_spring.entry;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Entry> getAllEntriesByCategoryID(Long id) {
        return entryRepository.findByCategoryId(id);
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
        entryCheckService.findEntryById(id);
        entryRepository.deleteById(id);
    }

    public void deleteAllEntriesByAccountId(Long id) {
        entryRepository.deleteByAccountId(id);
    }

    public void deleteAllEntriesByCategoryId(Long id) {
        entryRepository.deleteByCategoryId(id);
    }

}
