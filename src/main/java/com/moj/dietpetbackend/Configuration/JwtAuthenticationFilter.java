package com.moj.dietpetbackend.Configuration;

import com.moj.dietpetbackend.Entity.Users;
import com.moj.dietpetbackend.Enums.TokenType;
import com.moj.dietpetbackend.Repository.UserRepository;
import com.moj.dietpetbackend.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    public JwtAuthenticationFilter( JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // get auth from current authentication from spring security contex
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // only try authenticate if the user is null or anonymous
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            Long userId =  jwtService.getUserIdFromAccessTokenAndTempToken(request, TokenType.ACCESS);

            if (userId != null) {
                Users user = userRepository.findById(userId).orElse(null);
                // Create an Authentication object for the authenticated user
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getName(),
                                null,
                                List.of(new SimpleGrantedAuthority(user.getRole().name()))
                        );
                // Attach request-related details (IP, session, etc.)
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // saving a user as auth in spring security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // Continue the request through the remaining filters / controller
        filterChain.doFilter(request, response);
    }
}
