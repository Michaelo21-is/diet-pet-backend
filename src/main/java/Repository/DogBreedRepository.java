package Repository;

import Entity.DogBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogBreedRepository extends JpaRepository<DogBreed, Long> {
    List<DogBreed> findTop10ByDogBreedStartingWithIgnoreCase(String prefix);
}
