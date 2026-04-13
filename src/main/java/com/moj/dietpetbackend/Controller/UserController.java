package com.moj.dietpetbackend.Controller;

import com.moj.dietpetbackend.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/isLogedIn")
    public ResponseEntity<Boolean> isLogedIn(HttpServletRequest request){
        boolean response = userService.isUserLogedIn(request);
        return ResponseEntity.ok(response);
    }
}
