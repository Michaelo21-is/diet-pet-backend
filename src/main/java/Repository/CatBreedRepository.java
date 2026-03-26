package Repository;

import Entity.CatBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatBreedRepository extends JpaRepository<CatBreed, Long> {
    List<CatBreed> findTop10ByCatBreedStartingWithIgnoreCase(String prefix);
}
