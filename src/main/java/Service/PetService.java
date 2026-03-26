package Service;

import Dto.UploadNewPetDto;
import Entity.CatBreed;
import Entity.DogBreed;
import Entity.Users;
import Enums.PetType;
import Repository.CatBreedRepository;
import Repository.DogBreedRepository;
import Repository.PetRepository;
import Response.PetDailyNutritionRequirementsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class PetService {
    private final PetRepository petRepository;
    private final DogBreedRepository dogBreedRepository;
    private final CatBreedRepository catBreedRepository;
    public PetService(PetRepository petRepository , DogBreedRepository dogBreedRepository, CatBreedRepository catBreedRepository) {
        this.petRepository = petRepository;
        this.dogBreedRepository = dogBreedRepository;
        this.catBreedRepository = catBreedRepository;
    }
    public List<String> performPrefixToFindABreed(String prefix, PetType petType) {
        if (PetType.DOG.equals(petType)) {
            List<DogBreed> dogBreeds = dogBreedRepository.findTop10ByDogBreedStartingWithIgnoreCase(prefix);
            return dogBreeds.stream()
                    .map(DogBreed::getDogBreed)
                    .toList();

        } else if (PetType.CAT.equals(petType)) {
            List<CatBreed> catBreeds = catBreedRepository.findTop10ByCatBreedStartingWithIgnoreCase(prefix);
            return catBreeds.stream()
                    .map(CatBreed::getCatBreed)
                    .toList();
        }

        return null;
    }
    public String createNewPet(UploadNewPetDto uploadNewPetDto, Users user){
        Integer age = calculatePetAge(uploadNewPetDto.getBirthDate());
        PetDailyNutritionRequirementsResponse petDailyIntake = calculateDogIntake(uploadNewPetDto.getPetBreed(), uploadNewPetDto.getPetWeightKg(), age, uploadNewPetDto.getPetType());

    }
    public PetDailyNutritionRequirementsResponse calculateDogIntake(String petBreed, Double weight, Integer age, PetType petType){

    }
    public Integer calculatePetAge(LocalDate birthDate){
        LocalDate today = LocalDate.now();
        return Period.between(birthDate, today).getYears();
    }
}
