package com.easybudget.easybudget_spring.entry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
// No public modifier means package-private
interface EntryRepository extends JpaRepository<Entry, Long>, JpaSpecificationExecutor<Entry> {
    void deleteByAccountId(Long id);

    void deleteByCategoryId(Long id);
}
