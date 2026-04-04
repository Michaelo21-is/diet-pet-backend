package com.moj.dietpetbackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyzeFoodPictureDto {

    private MultipartFile file;
    private String foodName;
    private Double grams;

}
