package com.moj.dietpetbackend.Repository;

import com.moj.dietpetbackend.Entity.DogDailyWalkoutTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface DogDailyWalkoutTrackRepository extends JpaRepository<DogDailyWalkoutTrack, Long> {

    @Modifying
    @Query("""
        update DogDailyWalkoutTrack d
        set d.WalkoutTime = :walkoutTime,
            d.DistanceWalked = :distanceWalked,
            d.WalkoutDuration = :walkoutTimeToTake,
            d.calorieBurned = :calorieBurned
        where d.pet.id = :petId
          and d.intakeDate between :startOfDay and :endOfDay
    """)
    int updateTodayWalkout(
            @Param("petId") Long petId,
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay,
            @Param("walkoutTime") Integer walkoutTime,
            @Param("distanceWalked") Double distanceWalked,
            @Param("calorieBurned") Double calorieBurned,
            @Param("walkoutTimeToTake") Double walkoutTimeToTake
    );
    @Query("select d from DogDailyWalkoutTrack d where d.pet.user.id = :userId and d.intakeDate between :startOfDay and :endOfDay")
    Optional<DogDailyWalkoutTrack> findByUserId(Long userId, Instant startOfDay, Instant endOfDay);
}
