package com.easybudget.easybudget_spring.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybudget.easybudget_spring.entry.EntryService;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;

@Transactional
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryCheckService categoryCheckService;

    @Autowired
    private EntryService entryService;

    @Autowired
    private UserService userService;

    public List<CategoryDto> getAllCategories() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<Category> categories = categoryRepository.findAllByUser(currentUser);
        return categories.stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryCheckService.findCategoryById(categoryId);
        return CategoryMapper.toDto(category);
    }

    public CategoryDto createCategory(CreateCategoryRequestDto createCategoryRequestDto) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Category newCategory = Category.builder()
                .name(createCategoryRequestDto.getName())
                .user(currentUser)
                .build();
        Category savedCategory = categoryRepository.save(newCategory);
        return CategoryMapper.toDto(savedCategory);
    }

    // Becareful with updating or deleting a Category
    // How would you handle the connected Entries?
    // When updated, all the Entries should update their Category as well
    // When deleted, all the connected entries should be deleted too
    // ***These decisions are dangerous, users should be asked at least twice
    public CategoryDto updateCategory(Long categoryId, UpdateCategoryRequestDto updateCategoryRequestDto) {
        Category existingCategory = categoryCheckService.findCategoryById(categoryId);

        existingCategory.setName(updateCategoryRequestDto.getName());
        Category savedCategory = categoryRepository.save(existingCategory);
        return CategoryMapper.toDto(savedCategory);
    }

    public String deleteCategory(Long categoryId) {
        // you should check if it exists before deleting => DONE!
        Category deletingCategory = categoryCheckService.findCategoryById(categoryId);
        String categoryName = deletingCategory.getName();

        entryService.deleteAllEntriesByCategoryId(categoryId);
        categoryRepository.deleteById(categoryId);

        return categoryName + " Category deleted successfully";
    }

}
