package com.greenfox.dramacsoport.petclinicbackend.services.appUser;

import com.greenfox.dramacsoport.petclinicbackend.dtos.delete.DeleteUserResponse;
import com.greenfox.dramacsoport.petclinicbackend.exeptions.DeletionException;
import com.greenfox.dramacsoport.petclinicbackend.models.AppUser;
import com.greenfox.dramacsoport.petclinicbackend.models.Pet;
import com.greenfox.dramacsoport.petclinicbackend.repositories.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceTest {

    @InjectMocks
    private AppUserServiceImpl appUserService;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppUser appUser;

    @Captor
    private ArgumentCaptor<AppUser> appUserCaptor;

    @Test
    public void shouldNotAllowDeletionIfUserHasPets() {
        // Given
        String userEmail = "test@example.com";
        when(appUserRepository.findByEmail(anyString())).thenReturn(Optional.of(appUser));
        when(appUser.getPets()).thenReturn(List.of(new Pet()));

        // When
        DeletionException deletionException = assertThrows(DeletionException.class, () -> appUserService.deleteUser(userEmail));

        // Then
        assertEquals("Unable to delete your profile. Please transfer or delete your pets before proceeding.", deletionException.getMessage());
        verify(appUserRepository, never()).delete(appUserCaptor.capture());
    }

    @Test
    public void shouldAllowDeletionIfUserHasNoPets() throws DeletionException {
        // Given
        String userEmail = "test@example.com";
        when(appUserRepository.findByEmail(anyString())).thenReturn(Optional.of(appUser));
        when(appUser.getPets()).thenReturn(List.of());

        // When
        DeleteUserResponse deleteUserResponse = appUserService.deleteUser(userEmail);

        // Then
        assertEquals("Your profile has been successfully deleted.", deleteUserResponse.message());
        verify(appUserRepository).delete(appUserCaptor.capture());
        assertEquals(appUser, appUserCaptor.getValue());
    }
}
