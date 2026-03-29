package Service;

import Dto.LoginDto;
import Dto.RegisterDetailsDto;
import Entity.TwoFactorEmail;
import Entity.Users;
import Enums.Role;
import Enums.TwoFactorType;
import Repository.TwoFactorEmailRepository;
import Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TwoFactorEmailRepository twoFactorEmailRepository;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService , TwoFactorEmailRepository twoFactorEmailRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.twoFactorEmailRepository = twoFactorEmailRepository;
    }
    // sign up user checking if the email is already created then encode passowrd then save the user
    @Transactional
    public String RegisterUser(RegisterDetailsDto registerDetailsDto){
        if(userRepository.existsByEmail(registerDetailsDto.getEmail())){
            return "Email already exists";
        }
        String encodedPassword = passwordEncoder.encode(registerDetailsDto.getPassword());
        if(registerDetailsDto.getTimeZone() == null){
            return "please give access to your location";
        }
        Users users = Users.builder()
                .name(registerDetailsDto.getName())
                .email(registerDetailsDto.getEmail())
                .role(Role.REGULAR_USER)
                .password(encodedPassword)
                .timeZone(registerDetailsDto.getTimeZone())
                .dateOfCreation(LocalDate.now(ZoneId.of("Asia/Jerusalem")))
                .build();
        setTwoFactor(registerDetailsDto.getEmail(), users);
        userRepository.save(users);
        return "User created successfully";
    }
    @Transactional
    public String setTwoFactor(String email, Users user){
        String generatedCode = String.valueOf((int)(Math.random() * 900000) + 100000);
        TwoFactorEmail twoFactorEmail = TwoFactorEmail.builder()
                .user(user)
                .expirationTime(Instant.now().plus(15, ChronoUnit.MINUTES))
                .generatedCode(generatedCode)
                .build();
        twoFactorEmailRepository.save(twoFactorEmail);
        if (twoFactorEmail.equals(TwoFactorType.VERIFY_EMAIL)) {emailService.sendVerifyEmail(email, generatedCode);}
        else{emailService.sendToChangePassword(email, generatedCode);}
        return "2FA code sent successfully";
    }
    public String verify2FA(Users user, String code){
        TwoFactorEmail twoFactorEmail = twoFactorEmailRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("2FA code not found"));
        if (twoFactorEmail.getExpirationTime().isBefore(Instant.now())){
            return "2FA code expired";
        }
        if (twoFactorEmail.getGeneratedCode().equals(code)){
            return "2FA code verified successfully";
        }
        twoFactorEmailRepository.delete(twoFactorEmail);
        return "Invalid 2FA code";
    }
    public String login(LoginDto loginDto){
        Users user = userRepository.findByEmail(loginDto.getEmail())
                .orElse(null);
        if (user != null && passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return "Login successful";
        }
        return "email or password is incorrect";
    }
    @Transactional
    public String setChangePassword(Users user, String newPassword){
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return "Password changed successfully";
    }
}
