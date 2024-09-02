package com.easybudget.easybudget_spring.entry;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryService {

    @Autowired
    private EntryRepository entryRepository;

    public List<Entry> getAllItems() {
        return entryRepository.findAll();
    }

    public Entry addEntry(Entry entry) {
        return entryRepository.save(entry);
    }

    // public void deleteItemById(Long id) {
    // itemRepository.deleteById(id);
    // }
}
