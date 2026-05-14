package com.moj.dietpetbackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePetDto {
    private Double petWighetKg;
    private Double petCalorieBalance;
    private Double petProteinBalance;
    private Double petFatBalance;

}
