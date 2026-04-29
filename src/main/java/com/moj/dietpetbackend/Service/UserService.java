package com.moj.dietpetbackend.Service;

import com.moj.dietpetbackend.Enums.TokenType;
import com.moj.dietpetbackend.Repository.PetRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final JwtService jwtService;
    private final PetRepository petRepository;
    public UserService(JwtService jwtService, PetRepository petRepository) {
        this.jwtService = jwtService;
        this.petRepository = petRepository;
    }
    public String isUserLogedIn(HttpServletRequest request){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        if(!petRepository.existsByUserId(userId)){
            return "user should need to setup his pet details";
        }
        return null;
    }
}
