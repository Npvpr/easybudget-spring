package com.easybudget.easybudget_spring.entry;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easybudget.easybudget_spring.EasybudgetSpringApplication;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    @Autowired
    private EntryService entryService;

    // private static final Logger log =
    // LoggerFactory.getLogger(EasybudgetSpringApplication.class);

    @GetMapping
    public List<Entry> getAllItems() {
        return entryService.getAllItems();
    }

}
