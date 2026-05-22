package com.moj.dietpetbackend.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WhatThePetEatThatDayResponse {
    private String foodName;
    private Double grams;
    private Double calories;
    private Double fat;
    private Double protein;
    private String foodImagePath;
    private String AiReview;
    private String time;
}