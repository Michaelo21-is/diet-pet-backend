package Util;

import Enums.PetType;
import Response.PetDailyNutritionRequirementsResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PetNutritionUtils {
    public PetDailyNutritionRequirementsResponse calculatePetIntake(
            Double weight,
            Double age,
            PetType petType,
            boolean isNeutered,
            boolean isTendToBeAFattyPet
    ) {
        double rer = 70 * Math.pow(weight, 0.75);
        double calorieFactor;

        boolean isDogPuppyEarly = age < 0.33; // עד ~4 חודשים
        boolean isDogPuppyLate = age >= 0.33 && age < 1.0;
        boolean isKitten = age < 1.0;

        double proteinPer1000Kcal;
        double fatPer1000Kcal;

        switch (petType) {
            case DOG:
                if (isDogPuppyEarly) {
                    calorieFactor = 3.0;
                    proteinPer1000Kcal = 56.3;
                    fatPer1000Kcal = 21.3;
                } else if (isDogPuppyLate) {
                    calorieFactor = 2.0;
                    proteinPer1000Kcal = 56.3;
                    fatPer1000Kcal = 21.3;
                } else {
                    calorieFactor = isNeutered ? 1.6 : 1.8;

                    if (isTendToBeAFattyPet) {
                        calorieFactor -= 0.2;
                    }

                    proteinPer1000Kcal = 45.0;
                    fatPer1000Kcal = 13.8;
                }
                break;

            case CAT:
                if (isKitten) {
                    calorieFactor = 2.5;
                    proteinPer1000Kcal = 75.0;
                    fatPer1000Kcal = 22.5;
                } else {
                    calorieFactor = isNeutered ? 1.2 : 1.4;

                    if (isTendToBeAFattyPet) {
                        calorieFactor -= 0.1;
                    }

                    proteinPer1000Kcal = 65.0;
                    fatPer1000Kcal = 22.5;
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported pet type: " + petType);
        }

        double dailyCalories = rer * calorieFactor;
        double dailyProteinGrams = (dailyCalories / 1000.0) * proteinPer1000Kcal;
        double dailyFatGrams = (dailyCalories / 1000.0) * fatPer1000Kcal;

        return PetDailyNutritionRequirementsResponse.builder()
                .calories(dailyCalories)
                .protein(dailyProteinGrams)
                .fat(dailyFatGrams)
                .build();
    }
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
