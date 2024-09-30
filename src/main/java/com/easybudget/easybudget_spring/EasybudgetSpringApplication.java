package com.easybudget.easybudget_spring;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.easybudget.easybudget_spring.account.Account;
import com.easybudget.easybudget_spring.account.AccountService;
import com.easybudget.easybudget_spring.category.Category;
import com.easybudget.easybudget_spring.category.CategoryService;
import com.easybudget.easybudget_spring.entry.Entry;
import com.easybudget.easybudget_spring.entry.EntryService;
import com.easybudget.easybudget_spring.entry.Type;

@SpringBootApplication
public class EasybudgetSpringApplication {

	private static final Logger log = LoggerFactory.getLogger(EasybudgetSpringApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EasybudgetSpringApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(EntryService entryService,
			CategoryService categoryService, AccountService accountService) {
		return args -> {
			String loadData = "no";
			if (loadData == "load") {
				categoryService.addCategory(new Category("Food"));
				categoryService.addCategory(new Category("Salary"));
				accountService.addAccount(new Account("Cash", new BigDecimal(1000)));

				entryService.addEntry(new Entry(
						Type.OUTCOME,
						accountService.getAccountById(1L),
						categoryService.getCategoryById(1L),
						new BigDecimal(10.5),
						LocalDateTime.parse("2015-02-20T06:30:00"),
						"Rice and Eggs"));

				entryService.addEntry(new Entry(
						Type.INCOME,
						accountService.getAccountById(1L),
						categoryService.getCategoryById(2L),
						new BigDecimal(20.9),
						LocalDateTime.parse("2025-01-01T09:30:00"),
						"Software Engineer"));

				List<Entry> entries = entryService.getAllEntries();
				entries.forEach(entry -> {
					log.info(entry.toString());
				});
				log.info(entries.toString());
			} else if (loadData == "delete all other entries") {
				for (Long i = 0L; i < 47; i++) {
					entryService.deleteEntry(i);
				}
				// log.info(entryService.getEntryById(46L).toString());
			} else {
				log.info("Sample items already exist!");
			}
		};
	}
}
