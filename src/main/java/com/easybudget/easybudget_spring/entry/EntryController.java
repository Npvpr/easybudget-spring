package com.easybudget.easybudget_spring.entry;

import java.util.List;
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
        log.info("It's in HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        log.info(entry.toString());
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

}
