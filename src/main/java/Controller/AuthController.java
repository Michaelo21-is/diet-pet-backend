package Controller;


import Dto.LoginDto;
import Dto.RegisterDetailsDto;
import Enums.TokenType;
import Response.RegisterResponse;
import Response.SignInResponse;
import Service.AuthService;
import Service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }
    @PostMapping("/sign-up")
    public ResponseEntity<RegisterResponse> signUp(@RequestBody RegisterDetailsDto registerDetailsDto) {
        RegisterResponse response = authService.RegisterUser(registerDetailsDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@RequestBody LoginDto loginDto) {
        SignInResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
    // for now i will use it only for change password
    @PostMapping("/set_two_factor")
    public ResponseEntity<?> setTwoFactor(HttpServletRequest request){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        authService.setTwoFactor(userId);
        return ResponseEntity.ok().build();
    }
    // using it both for change password and verify email
    @PostMapping("/validate_two_factor")
    public ResponseEntity<SignInResponse> validate2FA(@RequestParam ("code") String code, HttpServletRequest request){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.TEMPORARY);
        SignInResponse response = authService.verify2FA(userId, code);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change_password")
    public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestParam("new_code") String newPassword){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        String responseMessage = authService.setChangePassword(userId, newPassword);
        return ResponseEntity.ok(responseMessage);
    }
    @DeleteMapping("/sign-out")
    public ResponseEntity<?> signOut(HttpServletRequest request){
        Long userId = jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);
        authService.signOut(userId);
        return ResponseEntity.ok().build();
    }
}
