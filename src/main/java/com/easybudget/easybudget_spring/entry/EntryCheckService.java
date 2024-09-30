package com.easybudget.easybudget_spring.entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.exception.NotFoundException;

@Transactional
@Service
public class EntryCheckService {
    @Autowired
    private EntryRepository entryRepository;

    public Entry findEntryById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entry not found with id: " + id));
    }
}
