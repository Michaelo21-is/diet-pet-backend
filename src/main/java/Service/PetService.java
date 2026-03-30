package Service;

import Dto.StartAWalkOutDto;
import Dto.UploadNewPetDto;
import Entity.*;
import Enums.PetType;
import Repository.*;
import Response.AiAnalyzePictureResponse;
import Response.PetDailyNutritionRequirementsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.util.List;


@Service
public class PetService {
    private final PetRepository petRepository;
    private final DogBreedRepository dogBreedRepository;
    private final CatBreedRepository catBreedRepository;
    private final ImageService imageService;
    private final OpenAiService openAiService;
    private final PetFoodTrackerRepository petFoodTrackerRepository;
    private final UserRepository userRepository;
    public PetService(PetRepository petRepository , DogBreedRepository dogBreedRepository, CatBreedRepository catBreedRepository
            , ImageService imageService, OpenAiService openAiService , PetFoodTrackerRepository petFoodTrackerRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.dogBreedRepository = dogBreedRepository;
        this.catBreedRepository = catBreedRepository;
        this.imageService = imageService;
        this.openAiService = openAiService;
        this.petFoodTrackerRepository = petFoodTrackerRepository;
        this.userRepository = userRepository;
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
    public String createNewPet(UploadNewPetDto uploadNewPetDto, Long userId) throws Exception{
        if (userId == null){
            throw new IllegalArgumentException("User ID cannot be null");
        }
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Double age = calculatePetAge(uploadNewPetDto.getBirthDate());
        Integer calculateDailyWalkoutTime = 0;
        if (uploadNewPetDto.getPetType().equals(PetType.DOG)){

        }
        Image image = imageService.uploadImage(uploadNewPetDto.getPetImage(), uploadNewPetDto.getPetName());
        PetDailyNutritionRequirementsResponse petDailyIntake = calculatePetIntake( uploadNewPetDto.getPetWeightKg(), age, uploadNewPetDto.getPetType(), uploadNewPetDto.getNeutered(), uploadNewPetDto.isTendToBeAFattyPet());
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
                .user(user)
                .build();
        petRepository.save(pet);
        return "Pet created successfully";
    }
    public AiAnalyzePictureResponse uploadPictureOfFoodForPet(MultipartFile file, Long userId, Double grams, Double age) throws Exception{
        if (userId == null){
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        Pet pet = petRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Pet not found"));
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        AiAnalyzePictureResponse aiAnalyzePictureResponse = openAiService.analyzeFoodPicture(file, grams, pet.getPetBreed(), pet.getPetType(), age);
        PetFoodTracker aiAnalyze = PetFoodTracker
                .builder()
                .pet(pet)
                .foodName(aiAnalyzePictureResponse.getFoodName())
                .grams(aiAnalyzePictureResponse.getGrams())
                .protein(aiAnalyzePictureResponse.getProtein())
                .aiReview(aiAnalyzePictureResponse.getAiReview())
                .foodScore(aiAnalyzePictureResponse.getFoodScore())
                .foodSafetyLevel(aiAnalyzePictureResponse.getFoodSafetyLevel())
                .createdAt(Instant.now())
                .build();
        petFoodTrackerRepository.save(aiAnalyze);
        return aiAnalyzePictureResponse;
    }
    public void startAWalk(Long userId, StartAWalkOutDto walkStats) {
        Pet pet = petRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Pet not found"));

    }





    ///////////////////////////////////////////////////////////////////////// CALCULATING AREA // ///////////////////////////////////////////////////////////////////////////////
    // calculate the calorie and nutertion the pet need protein and fat for just now
    public PetDailyNutritionRequirementsResponse calculatePetIntake(
            Double weight,
            Double age,
            PetType petType,
            boolean isNeutered,
            boolean isTendToBeAFattyPet
    ) {
        double rer = 70 * Math.pow(weight, 0.75);
        double calorieFactor;

        boolean isDogPuppyEarly = age < 0.33; // עד ~4 חודשים
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

                    if (isTendToBeAFattyPet) {
                        calorieFactor -= 0.2;
                    }

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

                    if (isTendToBeAFattyPet) {
                        calorieFactor -= 0.1;
                    }

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
