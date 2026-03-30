package Dto;

import Enums.ActivityLevel;
import Enums.PetType;
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
    private Boolean neutered;
    private MultipartFile petImage;
    private boolean tendToBeAFattyPet;
    private ActivityLevel activityLevel;
    private Double averageWalkoutDistance;
    private Double averageWalkoutTime;
}
