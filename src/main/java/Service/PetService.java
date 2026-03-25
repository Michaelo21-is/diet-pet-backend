package Service;

import Dto.UploadNewPetDto;
import Entity.PetDailyIntake;
import Entity.Users;
import Enums.PetType;
import Repository.PetRepository;
import Response.PetDailyNutritionRequirementsResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

@Service
public class PetService {
    private final PetRepository petRepository;
    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
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
