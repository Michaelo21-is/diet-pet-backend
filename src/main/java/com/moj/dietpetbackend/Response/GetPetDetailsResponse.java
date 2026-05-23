package com.moj.dietpetbackend.Response;

import com.moj.dietpetbackend.Enums.PetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetPetDetailsResponse {
    private Double petWeightKg;
    private Double dailyCaloriesIntake;
    private Double dailyProteinIntake;
    private Double dailyFatIntake;
    private PetType petType;
}
