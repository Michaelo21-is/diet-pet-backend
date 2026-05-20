package com.moj.dietpetbackend.Controller;

import com.moj.dietpetbackend.Dto.StartAWalkOutDto;
import com.moj.dietpetbackend.Enums.TokenType;
import com.moj.dietpetbackend.Response.GetDogDailyWalkoutTrackResponse;
import com.moj.dietpetbackend.Response.WalkOutOverviewResponse;
import com.moj.dietpetbackend.Service.DogService;
import com.moj.dietpetbackend.Service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dog")
public class DogController {
    private final DogService dogService;
    private final JwtService jwtService;
    public DogController(DogService dogService, JwtService jwtService) {
        this.dogService = dogService;
        this.jwtService = jwtService;
    }
    @PostMapping("/start_walk")
    public ResponseEntity<WalkOutOverviewResponse> startAWalk(HttpServletRequest request, @RequestBody StartAWalkOutDto walkStats) throws Exception{
        System.out.println("start walk have been called");
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        WalkOutOverviewResponse response = dogService.startAWalk(userId, walkStats);
        System.out.println("response: " + response);
        return ResponseEntity.ok(response);
    }
}
