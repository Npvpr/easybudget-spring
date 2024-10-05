package com.easybudget.easybudget_spring.entry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easybudget.easybudget_spring.EasybudgetSpringApplication;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/entry")
public class EntryController {
    private static final Logger log = LoggerFactory.getLogger(EasybudgetSpringApplication.class);

    @Autowired
    private EntryService entryService;

    @GetMapping("/all")
    public List<Entry> getAllEntries() {
        return entryService.getAllEntries();
    }

    @GetMapping
    public Entry getEntryById(@RequestParam Long id) {
        return entryService.getEntryById(id);
    }

    @PostMapping
    public Entry addEntry(@RequestBody Entry entry) {
        return entryService.addEntry(entry);
    }

    @PutMapping
    public Entry updateEntry(@RequestParam Long id, @RequestBody Entry entry) {
        return entryService.updateEntry(id, entry);
    }

    @DeleteMapping
    public void deleteEntry(@RequestParam Long id) {
        entryService.deleteEntry(id);
    }

    @GetMapping("/monthEntry")
    public Map<String, Object> getEntriesForMonthEntry(@RequestParam int year, @RequestParam int month) {
        return entryService.filterEntriesForMonthEntry(year, month);
    }

    @GetMapping("/graphs/month")
    public Map<String, Object> getEntriesForGraphsOfMonth(@RequestParam int year, @RequestParam int month) {
        return entryService.filterEntriesForGraphsOfMonth(year, month);
    }

    @GetMapping("/graphs/year")
    public Map<String, Object> getEntriesForGraphsOfYear(@RequestParam int year) {
        return entryService.filterEntriesForGraphsOfYear(year);
    }

    @GetMapping("/graphs/years")
    public Map<String, Object> getEntriesForGraphsOfYears(@RequestParam int startYear, @RequestParam int endYear) {
        return entryService.filterEntriesForGraphsOfYears(startYear, endYear);
    }

    @GetMapping("/history")
    public List<Entry> getEntriesForHistory(
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            String sortField, String sortOrder) {

        return entryService.filterEntriesForHistory(type, accountId, categoryId, startDate, endDate, sortField,
                sortOrder);
    }
}
