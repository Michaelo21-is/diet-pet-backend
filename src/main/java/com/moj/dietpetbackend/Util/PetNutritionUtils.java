package com.moj.dietpetbackend.Util;

import com.moj.dietpetbackend.Enums.PetType;
import com.moj.dietpetbackend.Response.PetDailyNutritionRequirementsResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PetNutritionUtils {
    public PetDailyNutritionRequirementsResponse calculateNewPetIntakeAfterWalkOut(Double caloriesBurned, PetType petType, Double age){
        Double proteinPer1000Kcal, fatPer1000Kcal;
        boolean isDogPuppyEarly = age < 0.33; // עד ~4 חודשים
        boolean isDogPuppyLate = age >= 0.33 && age < 1.0;
        boolean isKitten = age < 1.0;
        switch (petType) {
            case DOG:
                if (isDogPuppyEarly) {
                    proteinPer1000Kcal = 56.3;
                    fatPer1000Kcal = 21.3;
                } else if (isDogPuppyLate) {
                    proteinPer1000Kcal = 56.3;
                    fatPer1000Kcal = 21.3;
                } else {


                    proteinPer1000Kcal = 45.0;
                    fatPer1000Kcal = 13.8;
                }
                break;

            case CAT:
                if (isKitten) {
                    proteinPer1000Kcal = 75.0;
                    fatPer1000Kcal = 22.5;
                } else {

                    proteinPer1000Kcal = 65.0;
                    fatPer1000Kcal = 22.5;
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported pet type: " + petType);
        }
        Double addedProtein = (caloriesBurned / 1000.0) * proteinPer1000Kcal;

        Double addedFat = (caloriesBurned / 1000.0) * fatPer1000Kcal;
        return PetDailyNutritionRequirementsResponse.builder()
                .calories(caloriesBurned)
                .fat(addedFat)
                .protein(addedProtein)
                .build();
    }
}
