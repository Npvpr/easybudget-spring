package com.easybudget.easybudget_spring.category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.easybudget.easybudget_spring.entry.EntryService;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryCheckService categoryCheckService;

    @Mock
    private EntryService entryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryService categoryService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("john").build();
        testCategory = Category.builder().id(1L).name("Food").user(testUser).build();
    }

    @Test
    void testGetAllCategories_ShouldReturnCategoryList() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(categoryRepository.findAllByUser(testUser)).thenReturn(Arrays.asList(testCategory));

        List<CategoryDto> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getName());
        verify(categoryRepository, times(1)).findAllByUser(testUser);
    }

    @Test
    void testGetCategoryById_ShouldReturnCategory() {
        when(categoryCheckService.findCategoryById(1L)).thenReturn(testCategory);

        CategoryDto result = categoryService.getCategoryById(1L);

        assertEquals("Food", result.getName());
        verify(categoryCheckService, times(1)).findCategoryById(1L);
    }

    @Test
    void testCreateCategory_ShouldSaveAndReturnCategory() {
        CreateCategoryRequestDto dto = new CreateCategoryRequestDto("Transport");

        when(userService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryDto result = categoryService.createCategory(dto);

        assertNotNull(result);
        assertEquals("Transport", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_ShouldModifyAndSaveCategory() {
        UpdateCategoryRequestDto dto = new UpdateCategoryRequestDto("Entertainment");

        when(categoryCheckService.findCategoryById(1L)).thenReturn(testCategory);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryDto result = categoryService.updateCategory(1L, dto);

        assertEquals("Entertainment", result.getName());
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    void testDeleteCategory_ShouldRemoveCategoryAndEntries() {
        when(categoryCheckService.findCategoryById(1L)).thenReturn(testCategory);

        String result = categoryService.deleteCategory(1L);

        assertEquals("Food Category deleted successfully.", result);
        verify(entryService, times(1)).deleteAllEntriesByCategoryId(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
