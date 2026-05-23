package com.moj.dietpetbackend.Service;

import com.moj.dietpetbackend.Repository.DocumentDogDailyActivityRepository;
import com.moj.dietpetbackend.Repository.DogDailyWalkoutTrackRepository;
import com.moj.dietpetbackend.Repository.PetDailyIntakeRepository;
import com.moj.dietpetbackend.Repository.PetFoodTrackerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ScheduleService {
    private final DocumentDogDailyActivityRepository documentDogDailyActivityRepository;
    private final DogDailyWalkoutTrackRepository dogDailyWalkoutTrackRepository;
    private final PetDailyIntakeRepository petDailyIntakeRepository;
    private final PetFoodTrackerRepository petFoodTrackerRepository;
    public ScheduleService(DocumentDogDailyActivityRepository documentDogDailyActivityRepository,
                           PetDailyIntakeRepository petDailyIntakeRepository, PetFoodTrackerRepository petFoodTrackerRepository
    , DogDailyWalkoutTrackRepository dogDailyWalkoutTrackRepository) {
        this.documentDogDailyActivityRepository = documentDogDailyActivityRepository;
        this.petDailyIntakeRepository = petDailyIntakeRepository;
        this.petFoodTrackerRepository = petFoodTrackerRepository;
        this.dogDailyWalkoutTrackRepository = dogDailyWalkoutTrackRepository;
    }
    // run every day in 3 am global time to clean table that document every day activity, if 7 days has been pass i remove the row that expierd
    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    public void cleanDocumentDailyActivityTable(){
        Instant sevenDaysAgoInstant = Instant.now().minus(7, ChronoUnit.DAYS);
        LocalDate sevenDaysAgoLocalDate = LocalDate.now().minusDays(7);
        documentDogDailyActivityRepository.deleteAllByCreatedAtBefore(sevenDaysAgoInstant);
        petFoodTrackerRepository.deleteAllByCreatedAtBefore(sevenDaysAgoInstant);
        dogDailyWalkoutTrackRepository.deleteAllByIntakeDateBefore(sevenDaysAgoLocalDate);
        petDailyIntakeRepository.deleteAllByIntakeDateBefore(sevenDaysAgoLocalDate);
    }
}
