package com.moj.dietpetbackend.Service;

import com.moj.dietpetbackend.Dto.StartAWalkOutDto;
import com.moj.dietpetbackend.Entity.DogDailyWalkoutTrack;
import com.moj.dietpetbackend.Entity.DogWalkOutSuggestion;
import com.moj.dietpetbackend.Entity.Pet;
import com.moj.dietpetbackend.Repository.DogDailyWalkoutTrackRepository;
import com.moj.dietpetbackend.Repository.DogWalkOutSuggestionRepository;
import com.moj.dietpetbackend.Repository.PetDailyIntakeRepository;
import com.moj.dietpetbackend.Repository.PetRepository;
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
    public DogService(OpenAiService openAiService , PetRepository petRepository, DogDailyWalkoutTrackRepository dogDailyWalkoutTrackRepository
            , PetDailyIntakeRepository petDailyIntakeRepository, DogWalkOutSuggestionRepository dogWalkOutSuggestionRepository) {
        this.openAiService = openAiService;
        this.petRepository = petRepository;
        this.dogDailyWalkoutTrackRepository = dogDailyWalkoutTrackRepository;
        this.petDailyIntakeRepository = petDailyIntakeRepository;
        this.dogWalkOutSuggestionRepository = dogWalkOutSuggestionRepository;
    }
    @Transactional
    public WalkOutOverviewResponse startAWalk(Long userId, StartAWalkOutDto walkStats) throws Exception{
        Pet pet = petRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Pet not found"));
        Double age = PetAgeUtils.calculatePetAge(pet.getBirthDate());
        WalkOutOverviewResponse response = openAiService.getWalkOverViewByAi(walkStats.getKm(), walkStats.getDuration(), pet.getPetWeightKg(), age, pet.getPetBreed(), walkStats.getActivityLevel());
        ZoneId zone = ZoneId.of(pet.getUser().getTimeZone()); // לדוגמה Asia/Jerusalem
        LocalDate date = LocalDate.now(zone);

        Instant startOfDay = date.atStartOfDay(zone).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(zone).minusNanos(1).toInstant();
       int dailyWalkOutUpdate = dogDailyWalkoutTrackRepository.updateTodayWalkout(pet.getId(), startOfDay, endOfDay, response.getEquivalentStandardWalks(), walkStats.getKm(), walkStats.getDuration());
       if (dailyWalkOutUpdate == 0){
           throw new IllegalArgumentException("failed to update the daily walk out");
       }
       PetDailyNutritionRequirementsResponse calculatedAfterWalk = PetNutritionUtils.calculateNewPetIntakeAfterWalkOut(response.getCaloriesBurned(), pet.getPetType(), age);
       petDailyIntakeRepository.updatePetIntakeAfterWalkOut(pet.getId(), startOfDay, endOfDay, calculatedAfterWalk.getFat(), calculatedAfterWalk.getProtein(), calculatedAfterWalk.getCalories());

       return response;
    }
    @Transactional
    public GetDogDailyWalkoutTrackResponse getDogDailyWalkoutTrackResponse(Long userId){
        if (userId == null){
            throw new IllegalArgumentException("User ID cannot be null");
        }
        DogWalkOutSuggestion dogWalkOutSuggestion = dogWalkOutSuggestionRepository
                .findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User doesnt have a dog walk out suggestion need to build a new one"));
        Instant startOfDay = LocalDate.now(ZoneId.of(dogWalkOutSuggestion.getPet().getUser().getTimeZone())).atStartOfDay(ZoneId.of(dogWalkOutSuggestion.getPet().getUser().getTimeZone())).toInstant();
        Instant endOfDay = LocalDate.now(ZoneId.of(dogWalkOutSuggestion.getPet().getUser().getTimeZone())).plusDays(1).atStartOfDay(ZoneId.of(dogWalkOutSuggestion.getPet().getUser().getTimeZone())).minusNanos(1).toInstant();
        DogDailyWalkoutTrack dogDailyWalkoutTrack = dogDailyWalkoutTrackRepository.findByUserId(userId, startOfDay, endOfDay)
                .orElse(null);
        if (dogDailyWalkoutTrack == null){
            Pet pet = petRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("user doesnt have a pet"));
            dogDailyWalkoutTrack = new DogDailyWalkoutTrack();
            dogDailyWalkoutTrack.setWalkoutTimeToTake(0.0);
            dogDailyWalkoutTrack.setWalkoutTime(0);
            dogDailyWalkoutTrack.setDistanceWalked(0.0);
            dogDailyWalkoutTrack.setPet(pet);
            dogDailyWalkoutTrack.setIntakeDate(Instant.now());
        }
        return GetDogDailyWalkoutTrackResponse.builder()
                .dailyBalanceDailyWalkout(dogWalkOutSuggestion.getRecommendedWalkoutTime())
                .dailyBalanceWalkoutDistance(dogWalkOutSuggestion.getRecommendedDailyDistanceKm())
                .dailyBalanceWalkoutTime(dogWalkOutSuggestion.getRecommendedWalkoutTimeToTake())
                .dailyIntakeWalkout(dogDailyWalkoutTrack.getWalkoutTime())
                .dailyIntakeWalkoutDistance(dogDailyWalkoutTrack.getDistanceWalked())
                .dailyIntakeWalkoutTime(dogDailyWalkoutTrack.getWalkoutTimeToTake())
                .build();
    }
}
