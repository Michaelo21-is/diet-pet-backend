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
public class GetPetDailyTrackResponse {

    private Double caloriesBalance;
    private Double proteinBalance;
    private Double fatBalance;
    private Double caloriesIntake;
    private Double proteinIntake;
    private Double fatIntake;
    private Integer dailyBalanceDailyWalkout;
    private Double dailyBalanceWalkoutDistance;
    private Double dailyBalanceWalkoutTime;
    private Integer dailyIntakeWalkout;
    private Double dailyIntakeWalkoutDistance;
    private Double dailyIntakeWalkoutTime;
    private PetType petType;
}
