package com.moj.dietpetbackend.Service;

import com.moj.dietpetbackend.Dto.StartAWalkOutDto;
import com.moj.dietpetbackend.Entity.DocumentDogDailyActivity;
import com.moj.dietpetbackend.Entity.DogDailyWalkoutTrack;
import com.moj.dietpetbackend.Entity.DogWalkOutSuggestion;
import com.moj.dietpetbackend.Entity.Pet;
import com.moj.dietpetbackend.Repository.*;
import com.moj.dietpetbackend.Response.GetDogDailyWalkoutTrackResponse;
import com.moj.dietpetbackend.Response.PetDailyNutritionRequirementsResponse;
import com.moj.dietpetbackend.Response.WalkOutOverviewResponse;
import com.moj.dietpetbackend.Util.PetAgeUtils;
import com.moj.dietpetbackend.Util.PetNutritionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


@Service
public class DogService {
    private final OpenAiService openAiService;
    private final PetRepository petRepository;
    private final PetDailyIntakeRepository petDailyIntakeRepository;
    private final DogDailyWalkoutTrackRepository dogDailyWalkoutTrackRepository;
    private final DogWalkOutSuggestionRepository dogWalkOutSuggestionRepository;
    private final DocumentDogDailyActivityRepository documentDogDailyActivityRepository;
    public DogService(OpenAiService openAiService , PetRepository petRepository, DogDailyWalkoutTrackRepository dogDailyWalkoutTrackRepository
            , PetDailyIntakeRepository petDailyIntakeRepository, DogWalkOutSuggestionRepository dogWalkOutSuggestionRepository,
                      DocumentDogDailyActivityRepository documentDogDailyActivityRepository) {
        this.openAiService = openAiService;
        this.petRepository = petRepository;
        this.dogDailyWalkoutTrackRepository = dogDailyWalkoutTrackRepository;
        this.petDailyIntakeRepository = petDailyIntakeRepository;
        this.dogWalkOutSuggestionRepository = dogWalkOutSuggestionRepository;
        this.documentDogDailyActivityRepository = documentDogDailyActivityRepository;
    }
    @Transactional
    public WalkOutOverviewResponse startAWalk(Long userId, StartAWalkOutDto walkStats) throws Exception{
        Pet pet = petRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("Pet not found"));
        Double age = PetAgeUtils.calculatePetAge(pet.getBirthDate());
        WalkOutOverviewResponse response = openAiService.getWalkOverViewByAi(walkStats.getKm(), walkStats.getDuration(), pet.getPetWeightKg(), age, pet.getPetBreed(), walkStats.getActivityLevel());
        ZoneId zone = ZoneId.of(pet.getUser().getTimeZone()); // לדוגמה Asia/Jerusalem
        LocalDate date = LocalDate.now(zone);

        int dailyWalkOutUpdate = dogDailyWalkoutTrackRepository.updateTodayWalkout(
                pet.getId(),
                date,
                response.getEquivalentStandardWalks(),
                walkStats.getKm(),
                response.getCaloriesBurned(),
                walkStats.getDuration()
        );
        if (dailyWalkOutUpdate == 0){
           DogDailyWalkoutTrack dogDailyWalkoutTrack = DogDailyWalkoutTrack.builder()
                   .calorieBurned(response.getCaloriesBurned())
                   .DistanceWalked(walkStats.getKm())
                   .WalkoutDuration(walkStats.getDuration())
                   .pet(pet)
                   .WalkoutTime(response.getEquivalentStandardWalks())
                   .intakeDate(date)
                   .build();
           dogDailyWalkoutTrackRepository.save(dogDailyWalkoutTrack);
       }
       PetDailyNutritionRequirementsResponse calculatedAfterWalk = PetNutritionUtils.calculateNewPetIntakeAfterWalkOut(response.getCaloriesBurned(), pet.getPetType(), age);
        petDailyIntakeRepository.updatePetIntakeAfterWalkOut(
                pet.getId(),
                date,
                calculatedAfterWalk.getFat(),
                calculatedAfterWalk.getCalories(),
                calculatedAfterWalk.getProtein()
        );
        DocumentDogDailyActivity dogActivity = DocumentDogDailyActivity.builder()
                .pet(pet)
                .caloriesBurned(response.getCaloriesBurned())
                .distanceWalkedKm(walkStats.getKm())
                .durationMinutes(walkStats.getDuration())
                .aiReview(response.getAiReview())
                .createdAt(Instant.now())
                .build();
        documentDogDailyActivityRepository.save(dogActivity);
        return response;
    }
    @Transactional
    public GetDogDailyWalkoutTrackResponse getDogDailyWalkoutTrackResponse(Long userId, LocalDate date){
        DogWalkOutSuggestion dogWalkOutSuggestion = dogWalkOutSuggestionRepository
                .findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User doesnt have a dog walk out suggestion need to build a new one"));
        DogDailyWalkoutTrack dogDailyWalkoutTrack = dogDailyWalkoutTrackRepository.findByUserId(userId, date)
                .orElse(null);
        if (dogDailyWalkoutTrack == null){
            Pet pet = petRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("user doesnt have a pet"));
            dogDailyWalkoutTrack = new DogDailyWalkoutTrack();
            dogDailyWalkoutTrack.setWalkoutDuration(0.0);
            dogDailyWalkoutTrack.setWalkoutTime(0);
            dogDailyWalkoutTrack.setDistanceWalked(0.0);
            dogDailyWalkoutTrack.setPet(pet);
            dogDailyWalkoutTrack.setIntakeDate(date);
        }
        return GetDogDailyWalkoutTrackResponse.builder()
                .dailyBalanceDailyWalkout(dogWalkOutSuggestion.getRecommendedWalksPerDay())
                .dailyBalanceWalkoutDistance(dogWalkOutSuggestion.getRecommendedDailyDistanceKm())
                .dailyBalanceWalkoutTime(dogWalkOutSuggestion.getRecommendedWalkDurationMinutes())
                .dailyIntakeWalkout(dogDailyWalkoutTrack.getWalkoutTime())
                .dailyIntakeWalkoutDistance(dogDailyWalkoutTrack.getDistanceWalked())
                .dailyIntakeWalkoutTime(dogDailyWalkoutTrack.getWalkoutDuration())
                .build();
    }
}
