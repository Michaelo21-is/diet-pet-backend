package Repository;

import Entity.DogWalkOutSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DogWalkOutSuggestionRepository extends JpaRepository<DogWalkOutSuggestion, Long> {
    @Query("select d from DogWalkOutSuggestion d where d.pet.user.id = :userId")
    Optional<DogWalkOutSuggestion> findByUserId(Long userId);
}
