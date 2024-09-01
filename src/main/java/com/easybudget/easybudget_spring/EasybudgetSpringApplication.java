package com.easybudget.easybudget_spring;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.easybudget.easybudget_spring.item.Item;
import com.easybudget.easybudget_spring.item.ItemRepository;

@SpringBootApplication
public class EasybudgetSpringApplication {

	private static final Logger log = LoggerFactory.getLogger(EasybudgetSpringApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EasybudgetSpringApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(ItemRepository itemRepository) {
		return args -> {
			// if (itemRepository.count() == 0) {
			itemRepository.save(new Item("First One", 10.0f));
			itemRepository.save(new Item("Second One", 20.8f));
			List<Item> items = itemRepository.findAll();
			items.forEach(item -> {
				log.info(item.toString());
			});
			log.info(items.toString());
			// } else {
			// log.info("Sample items already exist!");
			// }
		};
	}
}
