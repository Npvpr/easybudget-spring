package com.easybudget.easybudget_spring.entry;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.exception.NotFoundException;

@Service
public class EntryService {

    @Autowired
    private EntryRepository entryRepository;

    public List<Entry> getAllEntries() {
        return entryRepository.findAll();
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
}
