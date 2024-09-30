package com.easybudget.easybudget_spring.category;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.EasybudgetSpringApplication;
import com.easybudget.easybudget_spring.entry.EntryService;

@Transactional
@Service
public class CategoryService {
    private static final Logger log = LoggerFactory.getLogger(EasybudgetSpringApplication.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryCheckService categoryCheckService;

    @Autowired
    private EntryService entryService;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryCheckService.findCategoryById(id);
    }

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Becareful with updating or deleting a Category
    // How would you handle the connected Entries?
    // When updated, all the Entries should update their Category as well
    // When deleted, all the connected entries should be deleted too
    // ***These decisions are dangerous, users should be asked at least twice
    public Category updateCategory(Long id, Category category) {
        Category existingCategory = categoryCheckService.findCategoryById(id);

        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        // you should check if it exists before deleting => DONE!
        categoryCheckService.findCategoryById(id);

        entryService.deleteAllEntriesByCategoryId(id);
        categoryRepository.deleteById(id);
    }

}
