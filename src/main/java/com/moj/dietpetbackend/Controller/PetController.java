package com.moj.dietpetbackend.Controller;

import com.moj.dietpetbackend.Dto.AnalyzeFoodPictureDto;
import com.moj.dietpetbackend.Dto.UploadNewPetDto;
import com.moj.dietpetbackend.Enums.PetType;
import com.moj.dietpetbackend.Enums.TokenType;
import com.moj.dietpetbackend.Response.AiAnalyzePictureResponse;
import com.moj.dietpetbackend.Response.GetPetDailyTrackResponse;
import com.moj.dietpetbackend.Response.PetOverviewResponse;
import com.moj.dietpetbackend.Service.JwtService;
import com.moj.dietpetbackend.Service.PetService;
import jakarta.servlet.http.HttpServletRequest;
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
    @PostMapping("/analyze-food-picture")
    public ResponseEntity<AiAnalyzePictureResponse> analyzeFoodPicture(HttpServletRequest request, @RequestBody AnalyzeFoodPictureDto analyzeFoodPictureDto) throws Exception{
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        AiAnalyzePictureResponse response = petService.uploadPictureOfFoodForPet(userId, analyzeFoodPictureDto);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get-pet-daily-diet-track")
    public ResponseEntity<GetPetDailyTrackResponse> getPetDailyTrackResponseResponseEntity(HttpServletRequest request){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        GetPetDailyTrackResponse response = petService.getPetDailyTrackResponse(userId);
        return ResponseEntity.ok(response);
    }
}
