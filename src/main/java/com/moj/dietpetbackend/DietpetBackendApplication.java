package com.moj.dietpetbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class DietpetBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietpetBackendApplication.class, args);
    }

}
