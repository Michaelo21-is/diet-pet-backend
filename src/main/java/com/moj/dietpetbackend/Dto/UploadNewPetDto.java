package com.moj.dietpetbackend.Dto;

import com.moj.dietpetbackend.Enums.ActivityLevel;
import com.moj.dietpetbackend.Enums.PetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadNewPetDto {
    private PetType petType;
    private String petName;
    private String petBreed;
    private Double petWeightKg;
    private LocalDate birthDate;
    private boolean neutered;
    private MultipartFile petImage;
    private boolean tendToBeAFattyPet;
    private ActivityLevel activityLevel;
    private Double averageWalkoutDistance;
    private Double averageWalkoutTime;
    private boolean hasYard;
}
