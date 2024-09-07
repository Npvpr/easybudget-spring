package com.easybudget.easybudget_spring.entry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// No public modifier means package-private
interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByCategoryId(Long id);

    void deleteByCategoryId(Long id);
}
