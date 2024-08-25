package com.greenfox.dramacsoport.petclinicbackend.services.appUser;

import com.greenfox.dramacsoport.petclinicbackend.dtos.delete.DeleteUserResponse;
import com.greenfox.dramacsoport.petclinicbackend.exeptions.DeletionErrorException;

public interface AppUserService {
    DeleteUserResponse deleteUser(String userEmail, Long id) throws DeletionErrorException;

}
