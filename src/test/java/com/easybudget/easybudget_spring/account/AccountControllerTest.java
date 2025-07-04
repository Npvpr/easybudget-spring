package com.easybudget.easybudget_spring.account;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "1", roles = {"USER"})
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testAccountId;

    @BeforeEach
    public void setup() throws Exception {
        // Create one account before every test for consistent testing
        CreateAccountRequestDto dto = CreateAccountRequestDto.builder()
                .name("Test Account")
                .build();

        String response = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        AccountDto createdAccount = objectMapper.readValue(response, AccountDto.class);
        testAccountId = createdAccount.getId();
    }

    @Test
    public void testGetAllAccounts() throws Exception {
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNumber());
    }

    @Test
    public void testGetAccountById_success() throws Exception {
        mockMvc.perform(get("/api/accounts/{id}", testAccountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAccountId));
    }

    @Test
    public void testGetAccountById_notFound() throws Exception {
        mockMvc.perform(get("/api/accounts/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetTotalBalance() throws Exception {
        mockMvc.perform(get("/api/accounts/balance/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    public void testCreateAccount_success() throws Exception {
        CreateAccountRequestDto dto = CreateAccountRequestDto.builder()
                .name("Savings Account")
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Savings Account"))
                .andExpect(jsonPath("$.balance").isNumber());
    }

    @Test
    public void testCreateAccount_invalidInput() throws Exception {
        CreateAccountRequestDto dto = CreateAccountRequestDto.builder()
                .name("") // Invalid: blank name
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateAccount_success() throws Exception {
        UpdateAccountRequestDto dto = UpdateAccountRequestDto.builder()
                .name("Updated Account")
                .build();

        mockMvc.perform(put("/api/accounts/{id}", testAccountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Account"));
    }

    @Test
    public void testUpdateAccount_notFound() throws Exception {
        UpdateAccountRequestDto dto = UpdateAccountRequestDto.builder()
                .name("Updated Account")
                .build();

        mockMvc.perform(put("/api/accounts/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAccount_success() throws Exception {
        mockMvc.perform(delete("/api/accounts/{id}", testAccountId))
                .andExpect(status().isOk())
                .andExpect(content().string("Test Account Account deleted successfully"));
    }

    @Test
    public void testDeleteAccount_notFound() throws Exception {
        mockMvc.perform(delete("/api/accounts/{id}", 999999))
                .andExpect(status().isNotFound());
    }
}
