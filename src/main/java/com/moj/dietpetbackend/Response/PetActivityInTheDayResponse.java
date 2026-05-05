package com.moj.dietpetbackend.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class PetActivityInTheDayResponse {
    private List<DogWalkOutSessionInfoResponse> dogWalkOutSessionInfoResponseList;
    private List<WhatThePetEatThatDayResponse> whatThePetEatThatDayResponseList;
}
