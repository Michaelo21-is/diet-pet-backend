package com.moj.dietpetbackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDetailsDto {
    private String email;
    private String password;
    private String timeZone;
}
