package com.moj.dietpetbackend.Service;

import com.moj.dietpetbackend.Enums.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final JwtService jwtService;
    public UserService(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    public boolean isUserLogedIn(HttpServletRequest request){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        return userId != null;
    }
}
