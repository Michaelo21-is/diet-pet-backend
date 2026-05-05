package com.moj.dietpetbackend.Repository;

import com.moj.dietpetbackend.Entity.DocumentDogDailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DocumentDogDailyActivityRepository extends JpaRepository<DocumentDogDailyActivity, Long> {
    List<DocumentDogDailyActivity> findByPetIdAndCreatedAtBetween(Long petId, Instant startOfDay, Instant endOfDay);
}
