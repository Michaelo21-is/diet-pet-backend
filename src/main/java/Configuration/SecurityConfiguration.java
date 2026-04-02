package Configuration;

import Repository.TokenRepository;
import Repository.UserRepository;
import Service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.function.Supplier;

@Configuration
public class SecurityConfiguration {
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        return new JwtAuthenticationFilter(jwtService, userRepository);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        CsrfTokenRequestHandler spaRequestHandler = new CsrfTokenRequestHandler() {
            @Override
            public void handle(HttpServletRequest request,
                               HttpServletResponse response,
                               Supplier<CsrfToken> csrfToken) {
                delegate.handle(request, response, csrfToken);
            }

            @Override
            public String resolveCsrfTokenValue(HttpServletRequest request,
                                                CsrfToken csrfToken) {
                return requestHandler.resolveCsrfTokenValue(request, csrfToken);
            }
        };

        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(tokenRepository)
                        .csrfTokenRequestHandler(spaRequestHandler)
                        .ignoringRequestMatchers("/api/auth/sign-in", "/api/auth/sign-up")
                )
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .authorizeHttpRequests(auth -> auth
                        // auth endpoint
                        .requestMatchers("/api/auth/set_two_factor").authenticated()
                        .requestMatchers("/api/auth/validate_two_factor").authenticated()
                        .requestMatchers("/api/auth/change_password").authenticated()
                        .requestMatchers("api/auth/sign-out").authenticated()
                        //pet endpoint
                        .requestMatchers("/api/pet/perform-prefix-for-breed").authenticated()
                        .requestMatchers("/api/pet/create-new-pet").authenticated()
                        .requestMatchers("/api/pet/analyze-food-picture").authenticated()
                        .requestMatchers("/api/pet/get-pet-daily-diet-track").authenticated()
                        // dog endpoint
                        .requestMatchers("/api/dog/start_walk").authenticated()
                        .requestMatchers("/api/dog/get_dog_daily_walk_stats").authenticated()
                );
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // הוסף את כל ה-origins שצריכים גישה
        // בדוק אם אנחנו ב-Docker או בסביבת פיתוח
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    public class PasswordConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

}
