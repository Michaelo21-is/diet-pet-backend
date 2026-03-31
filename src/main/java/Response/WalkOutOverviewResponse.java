package Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalkOutOverviewResponse {
    private Double caloriesBurned;
    private Integer equivalentStandardWalks;
    private String aiReview;
}
