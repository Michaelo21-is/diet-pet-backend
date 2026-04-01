package Service;

import Dto.UploadNewPetDto;
import Entity.*;
import Enums.PetType;
import Repository.*;
import Response.*;
import Util.PetAgeUtils;
import Util.PetNutritionUtils;
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
    private final DogWalkOutSuggestionRepository dogWalkOutSuggestionRepository;
    private final PetDailyIntakeRepository petDailyIntakeRepository;
    public PetService(PetRepository petRepository , DogBreedRepository dogBreedRepository, CatBreedRepository catBreedRepository
            , ImageService imageService, OpenAiService openAiService , PetFoodTrackerRepository petFoodTrackerRepository, UserRepository userRepository
    , DogWalkOutSuggestionRepository dogWalkOutSuggestionRepository, PetDailyIntakeRepository petDailyIntakeRepository) {
        this.petRepository = petRepository;
        this.dogBreedRepository = dogBreedRepository;
        this.catBreedRepository = catBreedRepository;
        this.imageService = imageService;
        this.openAiService = openAiService;
        this.petFoodTrackerRepository = petFoodTrackerRepository;
        this.userRepository = userRepository;
        this.dogWalkOutSuggestionRepository = dogWalkOutSuggestionRepository;
        this.petDailyIntakeRepository = petDailyIntakeRepository;
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
    public PetOverviewResponse createNewPet(UploadNewPetDto uploadNewPetDto, Long userId) throws Exception{
        if (userId == null){
            throw new IllegalArgumentException("User ID cannot be null");
        }
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Double age = PetAgeUtils.calculatePetAge(uploadNewPetDto.getBirthDate());
        Image image = imageService.uploadImage(uploadNewPetDto.getPetImage(), uploadNewPetDto.getPetName());
        PetDailyNutritionRequirementsResponse petDailyIntake = PetNutritionUtils.calculatePetIntake( uploadNewPetDto.getPetWeightKg(), age, uploadNewPetDto.getPetType(), uploadNewPetDto.isNeutered(), uploadNewPetDto.isTendToBeAFattyPet());
        Pet pet = Pet.builder()
                .petType(uploadNewPetDto.getPetType())
                .petName(uploadNewPetDto.getPetName())
                .petBreed(uploadNewPetDto.getPetBreed())
                .petWeightKg(uploadNewPetDto.getPetWeightKg())
                .neutered(uploadNewPetDto.isNeutered())
                .birthDate(uploadNewPetDto.getBirthDate())
                .calorieBalance(petDailyIntake.getCalories())
                .proteinBalance(petDailyIntake.getProtein())
                .fatBalance(petDailyIntake.getFat())
                .image(image)
                .user(user)
                .build();
        petRepository.save(pet);
        AiAnalyzeRecommendedWalkoutResponse response = new AiAnalyzeRecommendedWalkoutResponse();
        if (uploadNewPetDto.getPetType().equals(PetType.DOG)){
            response = openAiService.calculateHowManyTimeTheDogNeedTOGoOut(uploadNewPetDto.getPetBreed(), age, uploadNewPetDto.isNeutered(), uploadNewPetDto.getPetWeightKg(), uploadNewPetDto.isHasYard());
            DogWalkOutSuggestion dogWalkOutTracking = DogWalkOutSuggestion.builder()
                    .recommendedWalkoutTimeToTake(response.getRecommendedWalkoutTimeToTake())
                    .recommendedDailyDistanceKm(response.getRecommendedWalkoutDistance())
                    .aiReview(response.getAiReview())
                    .pet(pet)
                    .build();
            dogWalkOutSuggestionRepository.save(dogWalkOutTracking);
        }
        return PetOverviewResponse.builder()
                .calorie(petDailyIntake.getCalories())
                .protein(petDailyIntake.getProtein())
                .fat(petDailyIntake.getFat())
                .recommendedWalkoutDistance(response.getRecommendedWalkoutDistance())
                .recommendedWalkoutTime(response.getRecommendedWalkoutTime())
                .recommendedWalkoutTimeToTake(response.getRecommendedWalkoutTimeToTake())
                .aiReview(response.getAiReview())
                .build();
    }
    @Transactional
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
    // get daily daily intake and if its not exist create a new one
    @Transactional
    public GetPetDailyTrackResponse getPetDailyTrackResponse(Long userId){
        if (userId == null){
            throw new IllegalArgumentException("User ID cannot be null");
        }
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Instant startOfDay = LocalDate.now(ZoneId.of(user.getTimeZone())).atStartOfDay(ZoneId.of(user.getTimeZone())).toInstant();
        Instant endOfDay = LocalDate.now(ZoneId.of(user.getTimeZone())).plusDays(1).atStartOfDay(ZoneId.of(user.getTimeZone())).minusNanos(1).toInstant();
        PetDailyIntake petDailyTracker = petDailyIntakeRepository.findByUserId(userId, startOfDay, endOfDay)
                .orElse(null);
        if (petDailyTracker == null){
            Pet pet = petRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("user doesnt have a pet"));
            petDailyTracker = new PetDailyIntake();
            petDailyTracker.setDailyCalorie(0.0);
            petDailyTracker.setDailyProtein(0.0);
            petDailyTracker.setDailyFat(0.0);
            petDailyTracker.setDailyBalanceCalories(pet.getCalorieBalance());
            petDailyTracker.setDailyProteinBalance(pet.getProteinBalance());
            petDailyTracker.setDailyFatBalance(pet.getFatBalance());
            petDailyTracker.setIntakeDate(Instant.now());
            petDailyTracker.setPet(pet);
            petDailyIntakeRepository.save(petDailyTracker);
        }
        return GetPetDailyTrackResponse.builder()
                .caloriesBalance(petDailyTracker.getDailyBalanceCalories())
                .proteinBalance(petDailyTracker.getDailyProteinBalance())
                .fatBalance(petDailyTracker.getDailyFatBalance())
                .caloriesIntake(petDailyTracker.getDailyCalorie())
                .proteinIntake(petDailyTracker.getDailyProtein())
                .fatIntake(petDailyTracker.getDailyFat())
                .build();
    }





}
