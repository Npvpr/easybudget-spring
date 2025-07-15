package com.easybudget.easybudget_spring.entry;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    @Autowired
    private EntryService entryService;

    @GetMapping
    public ResponseEntity<List<EntryDto>> getAllEntries() {
        return new ResponseEntity<>(entryService.getAllEntries(), HttpStatus.OK);
    }

    @GetMapping("/{entryId}")
    public ResponseEntity<EntryDto> getEntryById(@PathVariable Long entryId) {
        return new ResponseEntity<>(entryService.getEntryById(entryId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EntryDto> createEntry(@Valid @RequestBody CreateEntryRequestDto createEntryRequestDto) {
        return new ResponseEntity<>(entryService.createEntry(createEntryRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{entryId}")
    public ResponseEntity<EntryDto> updateEntry(@PathVariable Long entryId,
            @Valid @RequestBody UpdateEntryRequestDto updateEntryRequestDto) {
        return new ResponseEntity<>(entryService.updateEntry(entryId, updateEntryRequestDto), HttpStatus.OK);
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<String> deleteEntry(@PathVariable Long entryId) {
        return new ResponseEntity<>(entryService.deleteEntry(entryId), HttpStatus.OK);
    }

    @GetMapping("/monthEntry")
    public ResponseEntity<Map<String, Object>> getEntriesForMonthEntry(@RequestParam int year,
            @RequestParam int month) {
        return new ResponseEntity<>(entryService.filterEntriesForMonthEntry(year, month), HttpStatus.OK);
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
    public Map<String, Object> getEntriesForHistory(
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            String sortField, String sortOrder) {

        return entryService.filterEntriesForHistory(type, accountId, categoryId, startDate, endDate, sortField,
                sortOrder);
    }
}
