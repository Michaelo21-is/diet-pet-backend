package Service;

import Dto.UploadNewPetDto;
import Entity.*;
import Enums.PetType;
import Repository.CatBreedRepository;
import Repository.DogBreedRepository;
import Repository.PetRepository;
import Response.AiAnalyzePictureResponse;
import Response.PetDailyNutritionRequirementsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;


@Service
public class PetService {
    private final PetRepository petRepository;
    private final DogBreedRepository dogBreedRepository;
    private final CatBreedRepository catBreedRepository;
    private final ImageService imageService;
    private final OpenAiService openAiService;

    public PetService(PetRepository petRepository , DogBreedRepository dogBreedRepository, CatBreedRepository catBreedRepository
            , ImageService imageService, OpenAiService openAiService) {
        this.petRepository = petRepository;
        this.dogBreedRepository = dogBreedRepository;
        this.catBreedRepository = catBreedRepository;
        this.imageService = imageService;
        this.openAiService = openAiService;
    }
    // performing prefix de on the pet type
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
    // create new pet in the data base
    @Transactional
    public String createNewPet(UploadNewPetDto uploadNewPetDto, Users user) throws Exception{
        Double age = calculatePetAge(uploadNewPetDto.getBirthDate());
        Image image = imageService.uploadImage(uploadNewPetDto.getPetImage(), uploadNewPetDto.getPetName());
        PetDailyNutritionRequirementsResponse petDailyIntake = calculatePetIntake( uploadNewPetDto.getPetWeightKg(), age, uploadNewPetDto.getPetType(), uploadNewPetDto.getNeutered());
        Pet pet = Pet.builder()
                .petType(uploadNewPetDto.getPetType())
                .petName(uploadNewPetDto.getPetName())
                .petBreed(uploadNewPetDto.getPetBreed())
                .petWeightKg(uploadNewPetDto.getPetWeightKg())
                .neutered(uploadNewPetDto.getNeutered())
                .birthDate(uploadNewPetDto.getBirthDate())
                .calorieBalance(petDailyIntake.getCalories())
                .proteinBalance(petDailyIntake.getProtein())
                .fatBalance(petDailyIntake.getFat())
                .image(image)
                .build();
        petRepository.save(pet);
        return "Pet created successfully";
    }
    public AiAnalyzePictureResponse uploadPictureOfFoodForPet(MultipartFile file, Long userId, Double grams) throws Exception{
        if (userId == null){
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        AiAnalyzePictureResponse aiAnalyzePictureResponse = openAiService.analyzeFoodPicture(file, grams);
        return aiAnalyzePictureResponse;
    }






    ///////////////////////////////////////////////////////////////////////// CALCULATING AREA // ///////////////////////////////////////////////////////////////////////////////
    // calculate the calorie and nutertion the pet need protein and fat for just now
    public PetDailyNutritionRequirementsResponse calculatePetIntake(
            Double weight,
            Double age,
            PetType petType,
            boolean isNeutered
    ) {
        double rer = 70 * Math.pow(weight, 0.75);
        double calorieFactor;

        boolean isDogPuppyEarly = age < 0.33;       // עד ~4 חודשים
        boolean isDogPuppyLate = age >= 0.33 && age < 1.0;
        boolean isKitten = age < 1.0;

        double proteinPer1000Kcal;
        double fatPer1000Kcal;

        switch (petType) {
            case DOG:
                if (isDogPuppyEarly) {
                    calorieFactor = 3.0;
                    proteinPer1000Kcal = 56.3;
                    fatPer1000Kcal = 21.3;
                } else if (isDogPuppyLate) {
                    calorieFactor = 2.0;
                    proteinPer1000Kcal = 56.3;
                    fatPer1000Kcal = 21.3;
                } else {
                    calorieFactor = isNeutered ? 1.6 : 1.8;
                    proteinPer1000Kcal = 45.0;
                    fatPer1000Kcal = 13.8;
                }
                break;

            case CAT:
                if (isKitten) {
                    calorieFactor = 2.5;
                    proteinPer1000Kcal = 75.0;
                    fatPer1000Kcal = 22.5;
                } else {
                    calorieFactor = isNeutered ? 1.2 : 1.4;
                    proteinPer1000Kcal = 65.0;
                    fatPer1000Kcal = 22.5;
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported pet type: " + petType);
        }

        double dailyCalories = rer * calorieFactor;

        double dailyProteinGrams = (dailyCalories / 1000.0) * proteinPer1000Kcal;
        double dailyFatGrams = (dailyCalories / 1000.0) * fatPer1000Kcal;

        return PetDailyNutritionRequirementsResponse.builder()
                .calories(dailyCalories)
                .protein(dailyProteinGrams)
                .fat(dailyFatGrams)
                .build();
    }
    public Double calculatePetAge(LocalDate birthDate){
        LocalDate today = LocalDate.now();
        Period period = Period.between(birthDate, today);

        return period.getYears() + (period.getMonths() / 12.0);
    }
}
