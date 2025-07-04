package com.easybudget.easybudget_spring.entry;

import com.easybudget.easybudget_spring.account.Account;
import com.easybudget.easybudget_spring.account.AccountDto;
import com.easybudget.easybudget_spring.account.AccountService;
import com.easybudget.easybudget_spring.account.CreateAccountRequestDto;
import com.easybudget.easybudget_spring.category.CategoryDto;
import com.easybudget.easybudget_spring.category.CategoryService;
import com.easybudget.easybudget_spring.category.CreateCategoryRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "1", roles = { "USER" })
public class EntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryService categoryService;

    private Long testEntryId;

    @BeforeEach
    @Transactional
    public void setup() throws Exception {
        // Create and save required Account
        CreateAccountRequestDto createAccountRequestDto = CreateAccountRequestDto.builder()
                .name("Test Account")
                .build();
        AccountDto account = accountService.createAccount(createAccountRequestDto); // ensures ID is assigned

        // Create and save required Category
        CreateCategoryRequestDto createCategoryRequestDto = CreateCategoryRequestDto.builder()
                .name("Test Category")
                .build();
        CategoryDto category = categoryService.createCategory(createCategoryRequestDto);

        // Now create entry using valid IDs
        CreateEntryRequestDto dto = CreateEntryRequestDto.builder()
                .cost(BigDecimal.valueOf(100.0))
                .description("Test Entry")
                .type(Type.INCOME)
                .accountId(account.getId()) // use generated ID
                .categoryId(category.getId()) // use generated ID
                .dateTime(LocalDateTime.now())
                .build();

        String response = mockMvc.perform(post("/api/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        EntryDto createdEntry = objectMapper.readValue(response, EntryDto.class);
        testEntryId = createdEntry.getId();
    }

    @Test
    public void testGetAllEntries() throws Exception {
        mockMvc.perform(get("/api/entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNumber());
    }

    @Test
    public void testGetEntryById_success() throws Exception {
        mockMvc.perform(get("/api/entries/{entryId}", testEntryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEntryId));
    }

    @Test
    public void testGetEntryById_notFound() throws Exception {
        mockMvc.perform(get("/api/entries/{entryId}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateEntry_success() throws Exception {
        CreateEntryRequestDto dto = CreateEntryRequestDto.builder()
                .cost(BigDecimal.valueOf(100.0)) // Use a valid cost for test
                .description("New Entry")
                .type(Type.OUTCOME)
                .accountId(1L)
                .categoryId(1L)
                .dateTime(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("New Entry"))
                .andExpect(jsonPath("$.cost").value(BigDecimal.valueOf(100.0)));
    }

    @Test
    public void testCreateEntry_invalidInput() throws Exception {
        CreateEntryRequestDto dto = CreateEntryRequestDto.builder()
                .cost(BigDecimal.valueOf(100.0)) 
                .description("")
                .type(null)
                .accountId(null)
                .categoryId(null)
                .dateTime(null)
                .build();

        mockMvc.perform(post("/api/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateEntry_success() throws Exception {
        UpdateEntryRequestDto dto = UpdateEntryRequestDto.builder()
                .cost(BigDecimal.valueOf(100.0)) // Use a valid cost for your test
                .description("Updated Entry")
                .type(Type.INCOME)
                .accountId(1L)
                .categoryId(1L)
                .dateTime(LocalDateTime.now())
                .build();

        mockMvc.perform(put("/api/entries/{entryId}", testEntryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Entry"))
                .andExpect(jsonPath("$.cost").value(BigDecimal.valueOf(100.0))); // Use a valid cost for your test
    }

    @Test
    public void testUpdateEntry_notFound() throws Exception {
        UpdateEntryRequestDto dto = UpdateEntryRequestDto.builder()
                .cost(BigDecimal.valueOf(100.0)) // Use a valid cost for your test
                .description("Updated Entry")
                .type(Type.INCOME)
                .accountId(1L)
                .categoryId(1L)
                .dateTime(LocalDateTime.now())
                .build();

        mockMvc.perform(put("/api/entries/{entryId}", 999999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteEntry_success() throws Exception {
        mockMvc.perform(delete("/api/entries/{entryId}", testEntryId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteEntry_notFound() throws Exception {
        mockMvc.perform(delete("/api/entries/{entryId}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetEntriesForMonthEntry() throws Exception {
        mockMvc.perform(get("/api/entries/monthEntry")
                .param("year", "2025")
                .param("month", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testGetEntriesForGraphsOfMonth() throws Exception {
        mockMvc.perform(get("/api/entries/graphs/month")
                .param("year", "2025")
                .param("month", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testGetEntriesForGraphsOfYear() throws Exception {
        mockMvc.perform(get("/api/entries/graphs/year")
                .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testGetEntriesForGraphsOfYears() throws Exception {
        mockMvc.perform(get("/api/entries/graphs/years")
                .param("startYear", "2023")
                .param("endYear", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testGetEntriesForHistory() throws Exception {
        mockMvc.perform(get("/api/entries/history")
                .param("type", "INCOME")
                .param("accountId", "1")
                .param("categoryId", "1")
                .param("startDate", "2025-01-01T00:00:00")
                .param("endDate", "2025-12-31T23:59:59")
                .param("sortField", "date")
                .param("sortOrder", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testGetEntriesForHistory_noParams() throws Exception {
        // Test optional params omitted
        mockMvc.perform(get("/api/entries/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
}
