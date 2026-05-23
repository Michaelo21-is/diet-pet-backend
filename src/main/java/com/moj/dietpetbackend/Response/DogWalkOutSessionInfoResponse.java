package com.moj.dietpetbackend.Response;

import com.moj.dietpetbackend.Enums.ActivityLevels;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DogWalkOutSessionInfoResponse {
    private Double walkoutDistanceKm;
    private Double walkoutTimeMin;
    private Double caloriesBurned;
    private String aiReview;
    private ActivityLevels activityLevel;
    private String time;
}
