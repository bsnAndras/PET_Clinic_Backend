package com.greenfox.dramacsoport.petclinicbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfox.dramacsoport.petclinicbackend.models.AppUser;
import com.greenfox.dramacsoport.petclinicbackend.repositories.AppUserRepository;
import com.greenfox.dramacsoport.petclinicbackend.dtos.LoginRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.Optional;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AppUserRepository appUserRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void shouldReturnTokenIfCorrectCredentialsProvided() throws Exception {

        AppUser mockUser = new AppUser();
        mockUser.setEmail("xy@example.com");
        mockUser.setPassword(passwordEncoder.encode("ValidPassword1"));

        when(appUserRepository.findByEmail("xy@example.com")).thenReturn(Optional.of(mockUser));

        LoginRequestDTO loginRequest = new LoginRequestDTO("xy@example.com", "ValidPassword1");

        this.mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.token").isString());

    }

    @Test
    public void responseShouldBeForbiddenIfUserNotFound() throws Exception {
        when(appUserRepository.findByEmail("aaa@example.com")).thenReturn(Optional.empty());

        LoginRequestDTO loginRequest = new LoginRequestDTO("aaa@example.com", "password");

        this.mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    public void responseShouldBeForbiddenIfWrongPasswordProvided() throws Exception {

        AppUser mockUser = new AppUser();
        mockUser.setEmail("xy@example.com");
        mockUser.setPassword(passwordEncoder.encode("ValidPassword1"));

        when(appUserRepository.findByEmail("xy@example.com")).thenReturn(Optional.of(mockUser));

        LoginRequestDTO loginRequest = new LoginRequestDTO("xy@example.com", "wrongPassword");

        this.mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isForbidden());

    }
}
