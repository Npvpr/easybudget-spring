package com.easybudget.easybudget_spring.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    //
    // ----------- SIGN UP TESTS ----------
    //

    @Test
    public void testSignup_missingEmail() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .username("TestUser")
                .password("test1234")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSignup_missingPassword() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("test1@example.com")
                .username("TestUser")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSignup_valid() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("test2@example.com")
                .username("TestUser")
                .password("test1234")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testSignup_conflict_duplicateEmail() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("test3@example.com")
                .username("TestUser")
                .password("test1234")
                .build();

        // First signup should succeed
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second signup with same email should fail
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Email already exists")));
    }

    //
    // ----------- SIGN IN TESTS ----------
    //

    @Test
    public void testSignin_validCredentials() throws Exception {
        RegisterRequestDto register = RegisterRequestDto.builder()
                .email("test4@example.com")
                .username("TestUser")
                .password("test1234")
                .build();

        // Create user first
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequestDto login = LoginRequestDto.builder()
                .email("test4@example.com")
                .password("test1234")
                .build();

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }

    @Test
    public void testSignin_invalidPassword() throws Exception {
        RegisterRequestDto register = RegisterRequestDto.builder()
                .email("test5@example.com")
                .username("TestUser")
                .password("test1234")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequestDto login = LoginRequestDto.builder()
                .email("test5@example.com")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testSignin_nonExistentEmail() throws Exception {
        LoginRequestDto login = LoginRequestDto.builder()
                .email("nonexistent@example.com")
                .password("test1234")
                .build();

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSignin_missingFields() throws Exception {
        LoginRequestDto login = LoginRequestDto.builder()
                .email("") // or null
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }
}
