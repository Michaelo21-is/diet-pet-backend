package com.moj.dietpetbackend.Repository;

import com.moj.dietpetbackend.Entity.PetFoodTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PetFoodTrackerRepository extends JpaRepository<PetFoodTracker, Long> {
    List<PetFoodTracker> findByPetIdAndCreatedAtBetween(
            Long petId,
            Instant startOfDay,
            Instant endOfDay
    );
    void deleteAllByCreatedAtBefore(Instant sevenDaysAgo);
}
