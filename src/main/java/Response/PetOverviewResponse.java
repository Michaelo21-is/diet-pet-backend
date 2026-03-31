package Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetOverviewResponse {
    // 1 page after the user finish to complete the pet form and is about diet
    private Double calorie;
    private Double protein;
    private Double fat;

    // 2 page walkout recomendtion for the dog
    private Double recommendedWalkoutDistance;
    private Double recommendedWalkoutTime;
    private Double recommendedWalkoutTimeToTake;
    private String aiReview;
}
