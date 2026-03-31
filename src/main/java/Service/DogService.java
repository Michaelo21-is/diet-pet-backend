package Service;

import Dto.StartAWalkOutDto;
import Entity.DogDailyWalkoutTrack;
import Entity.Pet;
import Repository.DogDailyWalkoutTrackRepository;
import Repository.PetDailyIntakeRepository;
import Repository.PetRepository;
import Response.PetDailyNutritionRequirementsResponse;
import Response.WalkOutOverviewResponse;
import Util.PetAgeUtils;
import Util.PetNutritionUtils;
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
    public DogService(OpenAiService openAiService , PetRepository petRepository, DogDailyWalkoutTrackRepository dogDailyWalkoutTrackRepository , PetDailyIntakeRepository petDailyIntakeRepository) {
        this.openAiService = openAiService;
        this.petRepository = petRepository;
        this.dogDailyWalkoutTrackRepository = dogDailyWalkoutTrackRepository;
        this.petDailyIntakeRepository = petDailyIntakeRepository;
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
}
