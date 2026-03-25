package Service;

import Enums.TokenType;
import Response.AuthResponse;
import Entity.JwtToken;
import Entity.Users;
import Repository.TokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final TokenRepository tokenRepository;

    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Value("${security.jwt.secret-key}")
    private String secretKey;


    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(Users user, TokenType tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        if (user.getRole() != null) {
            claims.put("role", user.getRole().name());
        }
        Long expirationTime ;
        if (tokenType.equals(TokenType.ACCESS)) {
            expirationTime = 15L * 60 * 1000;
        }
        else if (tokenType.equals(TokenType.REFRESH)){
            expirationTime = 14L * 24 * 60 * 60 * 1000;
        }
        else{
            expirationTime = 10L * 60 * 1000;
        }
        return buildToken(claims, user.getEmail(), expirationTime);
    }


    @Transactional
    public void saveToken(String accessToken,String refreshToken, Users user ) {
        Instant accessExpiry = Instant.now().plus(15, ChronoUnit.MINUTES);
        Instant refreshExpiry = Instant.now().plus(14, ChronoUnit.DAYS);

        JwtToken jwtToken = tokenRepository.findByUser_Id(user.getId())
                .orElse(
                        JwtToken.builder()
                                .user(user)
                                .build()
                );

        jwtToken.setAccessToken(accessToken);
        jwtToken.setRefreshToken(refreshToken);
        jwtToken.setExpirationDateAccessToken(accessExpiry);
        jwtToken.setExpirationDateRefreshToken(refreshExpiry);

        tokenRepository.save(jwtToken);
    }
    @Transactional
    public void deleteToken(Users user){
        tokenRepository.deleteAllByUser_Id(user.getId());
    }
    public Users getUserFromAccessToken(String accessToken){
        JwtToken jwtToken = tokenRepository.findByAccessToken(accessToken)
                .orElse(null);
        if (jwtToken == null || jwtToken.getExpirationDateAccessToken().isBefore(Instant.now())) {
            return null;
        }
        return jwtToken.getUser();
    }
    @Transactional
    public AuthResponse renewAccessToken(String refreshToken){
        JwtToken jwtToken = tokenRepository.findByRefreshToken(refreshToken)
                .orElse(null);
        if (jwtToken == null || jwtToken.getExpirationDateRefreshToken().isBefore(Instant.now())) {
            return null;
        }
        String newAccessToken = generateToken(jwtToken.getUser(), TokenType.ACCESS);
        String newRefreshToken = generateToken(jwtToken.getUser(), TokenType.REFRESH);
        saveToken(newAccessToken, newRefreshToken, jwtToken.getUser());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}