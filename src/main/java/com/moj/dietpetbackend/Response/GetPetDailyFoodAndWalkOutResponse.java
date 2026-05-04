package com.moj.dietpetbackend.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetPetDailyFoodAndWalkOutResponse {
    private WalkOutOverviewResponse walkOutOverviewResponse;
    private AiAnalyzeRecommendedForPetResponse aiAnalyzeRecommendedForPetResponse;
}
