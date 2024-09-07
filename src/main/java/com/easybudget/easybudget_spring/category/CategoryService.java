package com.easybudget.easybudget_spring.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.entry.EntryService;
import com.easybudget.easybudget_spring.exception.NotFoundException;

@Transactional
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntryService entryService;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
    }

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Becareful with updating or deleting a Category
    // How would you handle the connected Entries?
    // When updated, all the Entries should update their Category as well
    // When deleted, all the connected entries should be deleted too
    // ***These decisions are dangerous, users should be asked twice
    public Category updateCategory(Long id, Category category) {
        Category existingCategory = this.getCategoryById(id);
        if (existingCategory != null) {
            existingCategory.setName(category.getName());
            return categoryRepository.save(existingCategory);
        } else {
            return null;
        }
    }

    public void deleteCategory(Long id) {
        entryService.deleteAllEntriesByCategoryId(id);
        categoryRepository.deleteById(id);
    }

}
