package com.moj.dietpetbackend.Repository;

import com.moj.dietpetbackend.Entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByUserId(Long userId);
}
