package com.easybudget.easybudget_spring;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
			CategoryService categoryService) {
		return args -> {
			// if (entryService.count() == 0) {
			categoryService.addCategory(new Category("Food"));
			categoryService.addCategory(new Category("Salary"));

			entryService.addEntry(new Entry(
					Type.OUTCOME,
					categoryService.findById(1L),
					10.5,
					LocalDateTime.parse("2015-02-20T06:30:00"),
					"Rice and Eggs"));

			entryService.addEntry(new Entry(
					Type.INCOME,
					categoryService.findById(2L),
					20.9,
					LocalDateTime.parse("2025-01-01T09:30:00"),
					"Software Engineer"));

			List<Entry> entries = entryService.getAllItems();
			entries.forEach(entry -> {
				log.info(entry.toString());
			});
			log.info(entries.toString());
			// } else {
			// log.info("Sample items already exist!");
			// }
		};
	}
}
