package Dto;

import Enums.ActivityLevel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartAWalkOutDto {
    private Double km;
    private Double duration;
    private ActivityLevel activityLevel;
}
