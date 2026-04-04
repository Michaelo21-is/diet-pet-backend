package com.moj.dietpetbackend.Service;

import com.moj.dietpetbackend.Dto.LoginDto;
import com.moj.dietpetbackend.Dto.RegisterDetailsDto;
import com.moj.dietpetbackend.Entity.TwoFactorEmail;
import com.moj.dietpetbackend.Entity.Users;
import com.moj.dietpetbackend.Enums.Role;
import com.moj.dietpetbackend.Enums.TokenType;
import com.moj.dietpetbackend.Enums.TwoFactorType;
import com.moj.dietpetbackend.Repository.TwoFactorEmailRepository;
import com.moj.dietpetbackend.Repository.UserRepository;
import com.moj.dietpetbackend.Response.RegisterResponse;
import com.moj.dietpetbackend.Response.SignInResponse;
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
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService
            , TwoFactorEmailRepository twoFactorEmailRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.twoFactorEmailRepository = twoFactorEmailRepository;
        this.jwtService = jwtService;
    }
    // sign up user checking if the email is already created then encode passowrd then save the user
    @Transactional
    public RegisterResponse RegisterUser(RegisterDetailsDto registerDetailsDto){
        if(userRepository.existsByEmail(registerDetailsDto.getEmail())){
            return RegisterResponse.builder()
                    .message("Email already exists in the system")
                    .tempToken(null)
                    .build();
        }
        String encodedPassword = passwordEncoder.encode(registerDetailsDto.getPassword());
        if(registerDetailsDto.getTimeZone() == null){
            return RegisterResponse.builder()
                    .message("please give acessible time zone for the user")
                    .tempToken(null)
                    .build();
        }
        Users users = Users.builder()
                .name(registerDetailsDto.getName())
                .email(registerDetailsDto.getEmail())
                .role(Role.REGULAR_USER)
                .password(encodedPassword)
                .timeZone(registerDetailsDto.getTimeZone())
                .dateOfCreation(LocalDate.now(ZoneId.of("Asia/Jerusalem")))
                .build();
        setTwoFactor(users.getId());
        userRepository.save(users);
        String TempToken = jwtService.generateToken(users, TokenType.TEMPORARY);
        return RegisterResponse.builder()
                .message("User created successfully")
                .tempToken(TempToken)
                .build();
    }
    @Transactional
    public String setTwoFactor( Long userId){
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        String generatedCode = String.valueOf((int)(Math.random() * 900000) + 100000);
        TwoFactorEmail twoFactorEmail = TwoFactorEmail.builder()
                .user(user)
                .expirationTime(Instant.now().plus(15, ChronoUnit.MINUTES))
                .generatedCode(generatedCode)
                .build();
        twoFactorEmailRepository.save(twoFactorEmail);
        if (twoFactorEmail.equals(TwoFactorType.VERIFY_EMAIL)) {emailService.sendVerifyEmail(user.getEmail(), generatedCode);}
        else{emailService.sendToChangePassword(user.getEmail(), generatedCode);}
        return "2FA code sent successfully";
    }
    public SignInResponse verify2FA(Long userid, String code){
        if (userid == null || code == null){
            throw new IllegalArgumentException("User ID and code cannot be null");
        }
        Users user = userRepository.findById(userid).orElseThrow(() -> new IllegalArgumentException("User not found"));
        TwoFactorEmail twoFactorEmail = twoFactorEmailRepository.findByUserId(userid)
                .orElseThrow(() -> new RuntimeException("2FA code not found"));
        if (twoFactorEmail.getExpirationTime().isBefore(Instant.now())){
            twoFactorEmailRepository.delete(twoFactorEmail);
            return SignInResponse.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .message("2FA code expired")
                    .build();
        }
        if (!twoFactorEmail.getGeneratedCode().equals(code)){
            return SignInResponse.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .message("Invalid code")
                    .build();
        }
        twoFactorEmailRepository.delete(twoFactorEmail);
        return SignInResponse.builder()
                .accessToken(jwtService.generateToken(user, TokenType.ACCESS))
                .refreshToken(jwtService.generateToken(user, TokenType.REFRESH))
                .message("2FA code verified successfully")
                .build();
    }
    public SignInResponse login(LoginDto loginDto){
        Users user = userRepository.findByEmail(loginDto.getEmail())
                .orElse(null);
        if (user != null && passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return SignInResponse.builder()
                    .accessToken(jwtService.generateToken(user, TokenType.ACCESS))
                    .refreshToken(jwtService.generateToken(user, TokenType.REFRESH))
                    .message("Login successful")
                    .build();
        }
        return SignInResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .message("Invalid email or password")
                .build();
    }
    @Transactional
    public String setChangePassword(Long userId, String newPassword){
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return "Password changed successfully";
    }
    @Transactional
    public void signOut(Long userId){
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        jwtService.deleteToken(user);
    }
}
