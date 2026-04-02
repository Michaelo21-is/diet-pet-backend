package Controller;

import Dto.StartAWalkOutDto;
import Enums.TokenType;
import Response.GetDogDailyWalkoutTrackResponse;
import Response.WalkOutOverviewResponse;
import Service.DogService;
import Service.JwtService;
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
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        WalkOutOverviewResponse response = dogService.startAWalk(userId, walkStats);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get_dog_daily_walk_stats")
    public ResponseEntity<GetDogDailyWalkoutTrackResponse> getDogDailyWalkoutTrack(HttpServletRequest request){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        GetDogDailyWalkoutTrackResponse response = dogService.getDogDailyWalkoutTrackResponse(userId);
        return ResponseEntity.ok(response);
    }
}
