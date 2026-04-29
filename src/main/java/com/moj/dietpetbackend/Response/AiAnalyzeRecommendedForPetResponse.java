package com.moj.dietpetbackend.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiAnalyzeRecommendedForPetResponse {
    private Double recommendedWalkoutDistance;
    private Integer recommendedWalksPerDay;
    private Double recommendedWalkDurationMinutes;
    private Double recommendedDailyCalories;
    private Double recommendedDailyFat;
    private Double recommendedDailyProtein;
    private String aiReview;
}
