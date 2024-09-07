package com.easybudget.easybudget_spring.entry;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.exception.NotFoundException;

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

    public List<Entry> getAllEntries() {
        return entryRepository.findAll();
    }

    public List<Entry> getAllEntriesByCategoryID(Long id) {
        return entryRepository.findByCategoryId(id);
    }

    public Entry getEntryById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entry not found with id: " + id));
    }

    public Entry addEntry(Entry entry) {
        return entryRepository.save(entry);
    }

    public Entry updateEntry(Long id, Entry entry) {
        Entry existingEntry = this.getEntryById(id);
        if (existingEntry != null) {
            existingEntry.setCategory(entry.getCategory());
            existingEntry.setCost(entry.getCost());
            existingEntry.setDateTime(entry.getDateTime());
            existingEntry.setDescription(entry.getDescription());
            existingEntry.setType(entry.getType());
            return entryRepository.save(existingEntry);
        } else {
            return null;
        }
    }

    public void deleteEntry(Long id) {
        entryRepository.deleteById(id);
    }

    public void deleteAllEntriesByCategoryId(Long id) {
        entryRepository.deleteByCategoryId(id);
    }
}
