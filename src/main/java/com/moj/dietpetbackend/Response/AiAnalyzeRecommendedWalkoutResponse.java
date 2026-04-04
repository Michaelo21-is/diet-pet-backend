package com.moj.dietpetbackend.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiAnalyzeRecommendedWalkoutResponse {
    private Double recommendedWalkoutDistance;
    private Double recommendedWalkoutTime;
    private Double recommendedWalkoutTimeToTake;
    private String aiReview;
}
