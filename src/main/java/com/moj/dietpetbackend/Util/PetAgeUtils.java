package com.moj.dietpetbackend.Util;



import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.Period;

@UtilityClass
public class PetAgeUtils {
    public Double calculatePetAge(LocalDate birthDate){
        LocalDate today = LocalDate.now();
        Period period = Period.between(birthDate, today);

        return period.getYears() + (period.getMonths() / 12.0);
    }
}
