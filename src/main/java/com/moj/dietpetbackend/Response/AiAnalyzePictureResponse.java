package com.moj.dietpetbackend.Response;

import com.moj.dietpetbackend.Enums.FoodSafetyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiAnalyzePictureResponse {
    private Double calories;
    private Double protein;
    private Double fat;
    private String foodName;
    private Double grams;
    private Integer foodScore;
    private FoodSafetyLevel foodSafetyLevel;
    private String aiReview;
    private Integer petDailyWalkout;
}
