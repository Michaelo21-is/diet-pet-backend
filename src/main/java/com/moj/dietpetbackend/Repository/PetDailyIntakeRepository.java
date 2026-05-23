package com.moj.dietpetbackend.Repository;

import com.moj.dietpetbackend.Entity.PetDailyIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PetDailyIntakeRepository extends JpaRepository<PetDailyIntake, Long> {
    @Modifying
    @Query("""
        update PetDailyIntake p
        set p.dailyFatBalance = p.dailyFatBalance + :fat,
            p.dailyBalanceCalories = p.dailyBalanceCalories + :burnedCalories,
            p.dailyProteinBalance = p.dailyProteinBalance + :protein
        where p.pet.id = :petId
          and p.intakeDate = :localDate 
    """)
    int updatePetIntakeAfterWalkOut(
            @Param("petId") Long petId,
            @Param("localDate") LocalDate localDate,
            @Param("fat") Double fat,
            @Param("burnedCalories") Double burnedCalories,
            @Param("protein") Double protein
    );

    @Query("""
    select p
    from PetDailyIntake p
    where p.pet.id = :petId
      and p.intakeDate = :localDate
""")
    Optional<PetDailyIntake> findByPetIdAndIntakeDate(
            @Param("petId") Long petId,
            @Param("localDate") LocalDate localDate
    );

    @Modifying
    @Query("""
        update PetDailyIntake p
        set p.dailyFat = p.dailyFat + :fat,
            p.dailyCalorie = p.dailyCalorie + :burnedCalories,
            p.dailyProtein = p.dailyCalorie + :protein
        where p.pet.id = :petId
          and p.intakeDate = :localDate
    """)
    int updatePetIntakeAfterEating(
            @Param("petId") Long petId,
            @Param("localDate") LocalDate localDate,
            @Param("fat") Double fat,
            @Param("burnedCalories") Double burnedCalories,
            @Param("protein") Double protein
    );
    void deleteAllByIntakeDateBefore(LocalDate sevenDaysAgo);
}
