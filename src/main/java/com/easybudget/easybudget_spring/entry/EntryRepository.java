package com.easybudget.easybudget_spring.entry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.easybudget.easybudget_spring.user.User;

@Repository
// No public modifier means package-private
interface EntryRepository extends JpaRepository<Entry, Long>, JpaSpecificationExecutor<Entry> {
    void deleteByAccountId(Long id);

    void deleteByCategoryId(Long id);

    List<Entry> findAllByUser(User user);

}
