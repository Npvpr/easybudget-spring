package com.easybudget.easybudget_spring.category;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "1", roles = {"USER"})
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testCategoryId;

    @BeforeEach
    public void setup() throws Exception {
        // Create a category for testing
        CreateCategoryRequestDto dto = CreateCategoryRequestDto.builder()
                .name("Test Category")
                .build();

        String response = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CategoryDto createdCategory = objectMapper.readValue(response, CategoryDto.class);
        testCategoryId = createdCategory.getId();
    }

    @Test
    public void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNumber());
    }

    @Test
    public void testGetCategoryById_success() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", testCategoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCategoryId));
    }

    @Test
    public void testGetCategoryById_notFound() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCategory_success() throws Exception {
        CreateCategoryRequestDto dto = CreateCategoryRequestDto.builder()
                .name("New Category")
                .build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    public void testCreateCategory_invalidInput() throws Exception {
        CreateCategoryRequestDto dto = CreateCategoryRequestDto.builder()
                .name("") // invalid blank name
                .build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateCategory_success() throws Exception {
        UpdateCategoryRequestDto dto = UpdateCategoryRequestDto.builder()
                .name("Updated Category")
                .build();

        mockMvc.perform(put("/api/categories/{id}", testCategoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Category"));
    }

    @Test
    public void testUpdateCategory_notFound() throws Exception {
        UpdateCategoryRequestDto dto = UpdateCategoryRequestDto.builder()
                .name("Updated Category")
                .build();

        mockMvc.perform(put("/api/categories/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCategory_success() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", testCategoryId))
                .andExpect(status().isOk())
                .andExpect(content().string("Category deleted successfully"));
    }

    @Test
    public void testDeleteCategory_notFound() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", 999999))
                .andExpect(status().isNotFound());
    }
}
