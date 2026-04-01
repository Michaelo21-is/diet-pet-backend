package Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetPetDailyTrackResponse {

    private Double caloriesBalance;
    private Double proteinBalance;
    private Double fatBalance;
    private Double caloriesIntake;
    private Double proteinIntake;
    private Double fatIntake;
}
