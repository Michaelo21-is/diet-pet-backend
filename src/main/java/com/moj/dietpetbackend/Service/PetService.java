package com.moj.dietpetbackend.Service;

import com.moj.dietpetbackend.Dto.AnalyzeFoodPictureDto;
import com.moj.dietpetbackend.Dto.UploadNewPetDto;
import com.moj.dietpetbackend.Enums.PetType;
import com.moj.dietpetbackend.Util.PetAgeUtils;
import com.moj.dietpetbackend.Entity.*;
import com.moj.dietpetbackend.Repository.*;
import com.moj.dietpetbackend.Response.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class PetService {
    private final PetRepository petRepository;
    private final DogBreedRepository dogBreedRepository;
    private final CatBreedRepository catBreedRepository;
    private final OpenAiService openAiService;
    private final PetFoodTrackerRepository petFoodTrackerRepository;
    private final UserRepository userRepository;
    private final DogService dogService;
    private final DogWalkOutSuggestionRepository dogWalkOutSuggestionRepository;
    private final PetDailyIntakeRepository petDailyIntakeRepository;
    private final ImageService imageService;
    private final DocumentDogDailyActivityRepository documentDogDailyActivityRepository;
    public PetService(PetRepository petRepository , DogBreedRepository dogBreedRepository, CatBreedRepository catBreedRepository
            , OpenAiService openAiService , PetFoodTrackerRepository petFoodTrackerRepository, UserRepository userRepository
    , DogWalkOutSuggestionRepository dogWalkOutSuggestionRepository, PetDailyIntakeRepository petDailyIntakeRepository,
                      DogService dogService, ImageService imageService, DocumentDogDailyActivityRepository documentDogDailyActivityRepository) {
        this.petRepository = petRepository;
        this.dogBreedRepository = dogBreedRepository;
        this.catBreedRepository = catBreedRepository;
        this.openAiService = openAiService;
        this.petFoodTrackerRepository = petFoodTrackerRepository;
        this.userRepository = userRepository;
        this.dogWalkOutSuggestionRepository = dogWalkOutSuggestionRepository;
        this.petDailyIntakeRepository = petDailyIntakeRepository;
        this.dogService = dogService;
        this.imageService = imageService;
        this.documentDogDailyActivityRepository = documentDogDailyActivityRepository;
    }
    // performing prefix de on the pet type
    public List<String> performPrefixToFindABreed(String prefix, PetType petType) {
        System.out.println("performPrefixToFindABreed prefix: " + prefix + " petType: " + petType );
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
        AiAnalyzeFoodRecommendedForPetResponse response = new AiAnalyzeFoodRecommendedForPetResponse();
        response = openAiService.aiAnalyzeRecommendedForPetResponse(uploadNewPetDto.getPetBreed(), age, uploadNewPetDto.isNeutered(), uploadNewPetDto.getPetWeightKg(), uploadNewPetDto.isHasYard(), uploadNewPetDto.getPetType(), uploadNewPetDto.isTendToBeFat());
        Pet pet = Pet.builder()
            .petType(uploadNewPetDto.getPetType())
            .petName(uploadNewPetDto.getPetName())
            .petBreed(uploadNewPetDto.getPetBreed())
            .petWeightKg(uploadNewPetDto.getPetWeightKg())
            .neutered(uploadNewPetDto.isNeutered())
            .birthDate(uploadNewPetDto.getBirthDate())
            .calorieBalance(response.getRecommendedDailyCalories())
            .proteinBalance(response.getRecommendedDailyProtein())
            .fatBalance(response.getRecommendedDailyFat())
            .user(user)
            .build();
        petRepository.save(pet);
        DogWalkOutSuggestion dogWalkOutTracking = DogWalkOutSuggestion.builder()
                .recommendedWalksPerDay(response.getRecommendedWalksPerDay())
                .recommendedDailyDistanceKm(response.getRecommendedWalkoutDistance())
                .recommendedWalkDurationMinutes(response.getRecommendedWalkDurationMinutes())
                .aiReview(response.getAiReview())
                .pet(pet)
                .build();
        dogWalkOutSuggestionRepository.save(dogWalkOutTracking);
        return PetOverviewResponse.builder()
                .calorie(response.getRecommendedDailyCalories())
                .protein(response.getRecommendedDailyProtein())
                .fat(response.getRecommendedDailyFat())
                .recommendedWalkoutDistance(response.getRecommendedWalkoutDistance())
                .recommendedWalksPerDay(response.getRecommendedWalksPerDay())
                .recommendedWalkDurationMinutes(response.getRecommendedWalkDurationMinutes())
                .aiReview(response.getAiReview())
                .build();
    }
    @Transactional
    public AiAnalyzePictureFoodResponse uploadPictureOfFoodForPet(Long userId, AnalyzeFoodPictureDto data) throws Exception{
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (data.getFile().isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        Pet pet = petRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("Pet not found"));
        String contentType = data.getFile().getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        Double age = PetAgeUtils.calculatePetAge(pet.getBirthDate());
        AiAnalyzePictureFoodResponse aiAnalyzePictureFoodResponse = openAiService.analyzeFoodPicture(data.getFile(), data.getGrams(), pet.getPetBreed(), pet.getPetType(), age, data.getFoodName());
        Image image = imageService.uploadImage(data.getFile(), aiAnalyzePictureFoodResponse.getFoodName());
        PetFoodTracker aiAnalyze = PetFoodTracker
                .builder()
                .pet(pet)
                .calories(aiAnalyzePictureFoodResponse.getCalories())
                .foodName(aiAnalyzePictureFoodResponse.getFoodName())
                .fat(aiAnalyzePictureFoodResponse.getFat())
                .grams(aiAnalyzePictureFoodResponse.getGrams())
                .protein(aiAnalyzePictureFoodResponse.getProtein())
                .aiReview(aiAnalyzePictureFoodResponse.getAiReview())
                .foodScore(aiAnalyzePictureFoodResponse.getFoodScore())
                .foodSafetyLevel(aiAnalyzePictureFoodResponse.getFoodSafetyLevel())
                .image(image)
                .createdAt(Instant.now())
                .build();
        petFoodTrackerRepository.save(aiAnalyze);
        Instant startOfDay = LocalDate.now(ZoneId.of(user.getTimeZone())).atStartOfDay(ZoneId.of(user.getTimeZone())).toInstant();
        Instant endOfDay = LocalDate.now(ZoneId.of(user.getTimeZone())).plusDays(1).atStartOfDay(ZoneId.of(user.getTimeZone())).minusNanos(1).toInstant();
        petDailyIntakeRepository.updatePetIntakeAfterEating(pet.getId(), startOfDay, endOfDay, aiAnalyzePictureFoodResponse.getFat(), aiAnalyzePictureFoodResponse.getCalories(), aiAnalyzePictureFoodResponse.getProtein());
        return aiAnalyzePictureFoodResponse;
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
        Pet pet = petRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("user doesnt have a pet"));
        PetDailyIntake petDailyTracker = petDailyIntakeRepository.findByPetIdAndIntakeDateBetween(pet.getId(), startOfDay, endOfDay)
                .orElse(null);
        if (petDailyTracker == null){
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
        if(pet.getPetType() != PetType.DOG) {
            return GetPetDailyTrackResponse.builder()
                    .caloriesBalance(petDailyTracker.getDailyBalanceCalories())
                    .proteinBalance(petDailyTracker.getDailyProteinBalance())
                    .fatBalance(petDailyTracker.getDailyFatBalance())
                    .caloriesIntake(petDailyTracker.getDailyCalorie())
                    .proteinIntake(petDailyTracker.getDailyProtein())
                    .fatIntake(petDailyTracker.getDailyFat())
                    .petType(pet.getPetType())
                    .build();
        }
        GetDogDailyWalkoutTrackResponse dogDailyWalkoutTrackResponse = dogService.getDogDailyWalkoutTrackResponse(userId);
        return GetPetDailyTrackResponse.builder()
                .caloriesBalance(petDailyTracker.getDailyBalanceCalories())
                .proteinBalance(petDailyTracker.getDailyProteinBalance())
                .fatBalance(petDailyTracker.getDailyFatBalance())
                .caloriesIntake(petDailyTracker.getDailyCalorie())
                .proteinIntake(petDailyTracker.getDailyProtein())
                .fatIntake(petDailyTracker.getDailyFat())
                .petType(pet.getPetType())
                .dailyBalanceDailyWalkout(dogDailyWalkoutTrackResponse.getDailyBalanceDailyWalkout())
                .dailyBalanceWalkoutDistance(dogDailyWalkoutTrackResponse.getDailyBalanceWalkoutDistance())
                .dailyBalanceWalkoutTime(dogDailyWalkoutTrackResponse.getDailyBalanceWalkoutTime())
                .dailyIntakeWalkout(dogDailyWalkoutTrackResponse.getDailyIntakeWalkout())
                .dailyIntakeWalkoutDistance(dogDailyWalkoutTrackResponse.getDailyIntakeWalkoutDistance())
                .dailyIntakeWalkoutTime(dogDailyWalkoutTrackResponse.getDailyIntakeWalkoutTime())
                .build();
    }
    public PetActivityInTheDayResponse getPetDailyFoodAndWalkOutResponses (Long userId){
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Pet pet = petRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("user dosent have a pet"));
        // creating a time format hh is for hour and mm is for minute
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        // for knowing in which time zone is the user in
        ZoneId zoneId = ZoneId.of(user.getTimeZone());
        // take the date from the user timezone
        Instant startOfDay = LocalDate.now(zoneId)
                .atStartOfDay(zoneId) // make it to begin of the day for example 2026-05-04 00:00:00 Asia/Jerusalem
                .toInstant(); // convert it to Instant

        // take the date from the user timezone and then add one to the and minus nano second and then convert it to instant
        Instant endOfDay = LocalDate.now(zoneId)
                .plusDays(1)
                .atStartOfDay(zoneId)
                .minusNanos(1)
                .toInstant();
        List<PetFoodTracker> petFoodTrackers = petFoodTrackerRepository.findByPetIdAndCreatedAtBetween(pet.getId(), startOfDay, endOfDay);
        List<WhatThePetEatThatDayResponse> foodResponses =
                petFoodTrackers.stream()
                        .map(foodTracker -> WhatThePetEatThatDayResponse.builder()
                                .foodName(foodTracker.getFoodName())
                                .grams(foodTracker.getGrams())
                                .protein(foodTracker.getProtein())
                                .fat(foodTracker.getFat())
                                .AiReview(foodTracker.getAiReview())
                                .foodImagePath(foodTracker.getImage().getImageName())
                                .time(foodTracker.getCreatedAt().atZone(zoneId).format(formatter))
                                .build())
                        .toList();
        if(pet.getPetType() == PetType.CAT){
            return PetActivityInTheDayResponse.builder()
                    .whatThePetEatThatDayResponseList(foodResponses)
                    .dogWalkOutSessionInfoResponseList(null)
                    .build();
        }
        List<DocumentDogDailyActivity> dogDailyWalkoutTracks = documentDogDailyActivityRepository.findByPetIdAndCreatedAtBetween(pet.getId(), startOfDay, endOfDay);
        List<DogWalkOutSessionInfoResponse> dogWalkoutResponse =
                dogDailyWalkoutTracks.stream()
                        .map( dogWalkOut -> DogWalkOutSessionInfoResponse.builder()
                                .caloriesBurned(dogWalkOut.getCaloriesBurned())
                                .walkoutDistanceKm(dogWalkOut.getDistanceWalkedKm())
                                .walkoutTimeMin(dogWalkOut.getDurationMinutes())
                                .aiReview(dogWalkOut.getAiReview())
                                .time(dogWalkOut.getCreatedAt().atZone(zoneId).format(formatter))
                                .build()
                        ).toList();
        return PetActivityInTheDayResponse.builder()
                .whatThePetEatThatDayResponseList(foodResponses)
                .dogWalkOutSessionInfoResponseList(dogWalkoutResponse)
                .build();
    }

}
