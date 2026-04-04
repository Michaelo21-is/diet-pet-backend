package com.moj.dietpetbackend.Repository;

import com.moj.dietpetbackend.Entity.PetFoodTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetFoodTrackerRepository extends JpaRepository<PetFoodTracker, Long> {
}
