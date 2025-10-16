package com.greenfox.dramacsoport.petclinicbackend.services.applicationServices;

import com.greenfox.dramacsoport.petclinicbackend.models.AppUser;
import com.greenfox.dramacsoport.petclinicbackend.models.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    public void shouldCheckIfTokenIsValid() {
        AppUser userDetails = AppUser.builder()
                .displayName("testUser")
                .password("password")
                .role(Role.USER)
                .build();

        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    public void shouldGetUsername() {
        AppUser userDetails = AppUser.builder()
                .displayName("testUser")
                .password("password")
                .role(Role.USER)
                .build();

        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    public void shouldGetValidRoles() {
        for (Role role : Role.values()) {
            AppUser testUser = AppUser.builder()
                    .displayName("testUser")
                    .password("password")
                    .role(role)
                    .build();

            String token = jwtService.generateToken(testUser);

            Role extractedRole = jwtService.extractRole(token);
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + extractedRole.name());
            Set<GrantedAuthority> authorities = Collections.singleton(authority);

//            assertEquals(testUser.getAuthorities(), authorities);
            Assertions.assertArrayEquals(testUser.getAuthorities().toArray(), authorities.toArray());
        }
    }

    @Test
    public void shouldEncodeLowercaseRoles() {
        for (Role role : Role.values()) {
            AppUser testUser = AppUser.builder()
                    .displayName("testUser")
                    .password("password")
                    .role(role)
                    .build();

            String token = jwtService.generateToken(testUser);
            String extractedRole = jwtService.getClaims(token).get("role", String.class);

            String lowercaseRole = role.toString().toLowerCase();

            assertEquals(lowercaseRole, extractedRole);
        }
    }

    @Test
    @WithMockUser(username = "test@user.com")
    public void shouldClearSecurityContextWhenLoggingOut() {
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());

        //Act
        jwtService.logoutUser();

        //Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}