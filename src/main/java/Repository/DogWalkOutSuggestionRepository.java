package Repository;

import Entity.DogWalkOutSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogWalkOutSuggestionRepository extends JpaRepository<DogWalkOutSuggestion, Long> {
}
