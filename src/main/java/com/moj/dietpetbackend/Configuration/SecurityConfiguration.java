package com.moj.dietpetbackend.Configuration;

import com.moj.dietpetbackend.Repository.UserRepository;
import com.moj.dietpetbackend.Service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        return new JwtAuthenticationFilter(jwtService, userRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // public auth endpoints
                        .requestMatchers("/api/auth/sign-in", "/api/auth/sign-up").permitAll()

                        // protected auth endpoints
                        .requestMatchers("/api/auth/set_two_factor").authenticated()
                        .requestMatchers("/api/auth/validate_two_factor").authenticated()
                        .requestMatchers("/api/auth/change_password").authenticated()
                        .requestMatchers("/api/auth/sign-out").authenticated()

                        // pet endpoints
                        .requestMatchers("/api/pet/perform-prefix-for-breed").authenticated()
                        .requestMatchers("/api/pet/create-new-pet").authenticated()
                        .requestMatchers("/api/pet/analyze-food-picture").authenticated()
                        .requestMatchers("/api/pet/get-pet-daily-diet-track").authenticated()

                        // dog endpoints
                        .requestMatchers("/api/dog/start_walk").authenticated()
                        .requestMatchers("/api/dog/get_dog_daily_walk_stats").authenticated()

                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}