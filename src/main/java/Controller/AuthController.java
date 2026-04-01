package Controller;


import Dto.RegisterDetailsDto;
import Service.AuthService;
import Service.JwtService;
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
    public ResponseEntity<String> signUp(@RequestBody RegisterDetailsDto registerDetailsDto) {
        String message = authService.RegisterUser(registerDetailsDto);
        return ResponseEntity.ok(message);
    }
    @PostMapping("/validate_two_factor")
    public ResponseEntity<String> validate2FA(@RequestParam ("code") String code){

    }
}
