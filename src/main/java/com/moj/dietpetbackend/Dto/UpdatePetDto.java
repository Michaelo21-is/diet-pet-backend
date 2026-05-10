package com.moj.dietpetbackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePetDto {
    private Double newCaloriesBalance;
    private Double newProteinBalance;
    private Double newFatBalance;

}
