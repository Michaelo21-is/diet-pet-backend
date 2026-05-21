package com.moj.dietpetbackend.Controller;

import com.moj.dietpetbackend.Dto.AnalyzeFoodPictureDto;
import com.moj.dietpetbackend.Dto.UpdatePetDto;
import com.moj.dietpetbackend.Dto.UploadNewPetDto;
import com.moj.dietpetbackend.Enums.PetType;
import com.moj.dietpetbackend.Enums.TokenType;
import com.moj.dietpetbackend.Response.*;
import com.moj.dietpetbackend.Service.JwtService;
import com.moj.dietpetbackend.Service.PetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pet")
public class PetController {
    private final PetService petService;
    private final JwtService jwtService;
    public PetController(PetService petService, JwtService jwtService) {
        this.petService = petService;
        this.jwtService = jwtService;
    }
    @GetMapping("/perform-prefix-for-breed")
    public ResponseEntity<List<String>> getBreedFromPrefix(@RequestParam("prefix") String prefix, @RequestParam("petType") PetType petType){
        List<String> breed = petService.performPrefixToFindABreed(prefix, petType);
        return ResponseEntity.ok(breed);
    }
    @PostMapping("/create-new-pet")
    public ResponseEntity<PetOverviewResponse> createNewPet(HttpServletRequest request, @RequestBody UploadNewPetDto uploadNewPetDto) throws Exception{
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        PetOverviewResponse response = petService.createNewPet(uploadNewPetDto, userId);
        return ResponseEntity.ok(response);
    }
    @PostMapping(value = "/analyze-food-picture",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AiAnalyzePictureFoodResponse> analyzeFoodPicture(HttpServletRequest request, @ModelAttribute AnalyzeFoodPictureDto analyzeFoodPictureDto) throws Exception{
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        AiAnalyzePictureFoodResponse response = petService.uploadPictureOfFoodForPet(userId, analyzeFoodPictureDto);
        System.out.println("response: " + response);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get-pet-daily-diet-track")
    public ResponseEntity<GetPetDailyTrackResponse> getPetDailyTrackResponseResponseEntity(
            HttpServletRequest request,
            @RequestParam(value = "date", required = false) String date
    ) {
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        GetPetDailyTrackResponse response = petService.getPetDailyTrackResponse(userId, date);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get-pet-daily-activity")
    public ResponseEntity<PetActivityInTheDayResponse> getPetDailyActivity(
            HttpServletRequest request,
            @RequestParam(value = "date", required = false) String date){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        PetActivityInTheDayResponse response = petService.getPetDailyFoodAndWalkOutResponses(userId, date);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get-pet-details")
    public ResponseEntity<GetPetDetailsResponse> getPetDetails (HttpServletRequest request){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        GetPetDetailsResponse response = petService.getPetDetails(userId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/update-pet-details")
    public ResponseEntity<String> updatePetDetails ( HttpServletRequest request, @RequestBody UpdatePetDto updatePetDto){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        petService.updatePet(userId, updatePetDto);
        return ResponseEntity.ok("successfully updated pet details");
    }
}
