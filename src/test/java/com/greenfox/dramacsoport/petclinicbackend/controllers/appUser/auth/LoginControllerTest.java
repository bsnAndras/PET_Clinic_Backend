package com.greenfox.dramacsoport.petclinicbackend.controllers.appUser.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfox.dramacsoport.petclinicbackend.dtos.user.login.LoginRequestDTO;
import com.greenfox.dramacsoport.petclinicbackend.models.AppUser;
import com.greenfox.dramacsoport.petclinicbackend.repositories.AppUserRepository;
import com.greenfox.dramacsoport.petclinicbackend.services.appUser.AppUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AppUserRepository appUserRepository;
    @MockBean
    AppUserService appUserService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void shouldReturnTokenIfCorrectCredentialsProvided() throws Exception {

        AppUser mockUser = new AppUser();
        mockUser.setEmail("xy@example.com");
        mockUser.setPassword(passwordEncoder.encode("ValidPassword1"));

        when(appUserService.loadUserByEmail("xy@example.com")).thenReturn(mockUser);

        LoginRequestDTO loginRequest = new LoginRequestDTO("xy@example.com", "ValidPassword1");

        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.token").isString());
    }

    @Test
    public void responseShouldBeForbiddenIfUserNotFound() throws Exception {
        when(appUserService.loadUserByEmail("aaa@example.com")).thenThrow(UsernameNotFoundException.class);

        LoginRequestDTO loginRequest = new LoginRequestDTO("aaa@example.com", "password");

        this.mockMvc.perform(post("/api/v1/auth/login")
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

        when(appUserService.loadUserByEmail("xy@example.com")).thenReturn(mockUser);

        LoginRequestDTO loginRequest = new LoginRequestDTO("xy@example.com", "wrongPassword");

        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isForbidden());

    }

    @Test
    public void responseShouldBeBadRequestIfEmptyPasswordProvided() throws Exception {

        AppUser mockUser = new AppUser();
        mockUser.setEmail("xy@example.com");
        mockUser.setPassword(passwordEncoder.encode("ValidPassword1"));

        when(appUserService.loadUserByEmail("xy@example.com")).thenReturn(mockUser);

        LoginRequestDTO loginRequest = new LoginRequestDTO("xy@example.com", "");

        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isBadRequest());

    }

    @Test
    public void responseShouldBeBadRequestIfNullPasswordProvided() throws Exception {

        AppUser mockUser = new AppUser();
        mockUser.setEmail("xy@example.com");
        mockUser.setPassword(passwordEncoder.encode("ValidPassword1"));

        when(appUserService.loadUserByEmail("xy@example.com")).thenReturn(mockUser);

        LoginRequestDTO loginRequest = new LoginRequestDTO("xy@example.com", null);

        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isBadRequest());

    }

    @Test
    public void responseShouldBeBadRequestIfEmptyEmailProvided() throws Exception {

        AppUser mockUser = new AppUser();
        mockUser.setEmail("xy@example.com");
        mockUser.setPassword(passwordEncoder.encode("ValidPassword1"));

        when(appUserService.loadUserByEmail("xy@example.com")).thenReturn(mockUser);

        LoginRequestDTO loginRequest = new LoginRequestDTO("", "password");

        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isBadRequest());

    }
}
