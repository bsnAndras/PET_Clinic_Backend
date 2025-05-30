package com.greenfox.dramacsoport.petclinicbackend.repositories;

import com.greenfox.dramacsoport.petclinicbackend.models.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findAllByOwnerId(Long ownerId);
    Pet findPetById(Long petId);
}
