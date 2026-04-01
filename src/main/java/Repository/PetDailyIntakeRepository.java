package Repository;

import Entity.PetDailyIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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
          and p.intakeDate between :startOfDay and :endOfDay
    """)
    int updatePetIntakeAfterWalkOut(
            @org.springframework.data.repository.query.Param("petId") Long petId,
            @org.springframework.data.repository.query.Param("startOfDay") java.time.Instant startOfDay,
            @org.springframework.data.repository.query.Param("endOfDay") java.time.Instant endOfDay,
            @org.springframework.data.repository.query.Param("fat") Double fat,
            @org.springframework.data.repository.query.Param("burnedCalories") Double burnedCalories,
            @org.springframework.data.repository.query.Param("protein") Double protein
    );
    @Query("""
    select p
    from PetDailyIntake p
    where p.pet.user.id = :userId
      and p.intakeDate between :startOfDay and :endOfDay
""")
    Optional<PetDailyIntake> findByUserId(
            @Param("userId") Long userId,
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay
    );


}
