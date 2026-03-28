package Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetInfoResponse {
    private String petName;
    private String petBreed;
    private Double petAge;
    private Double petWeightKg;
    private Double dailyCaloriesIntake;
    private Double dailyFatIntake;
    private Double dailyProteinIntake;
    private String petImageName;
}
