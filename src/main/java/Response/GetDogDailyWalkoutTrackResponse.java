package Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetDogDailyWalkoutTrackResponse {
    private Integer dailyBalanceDailyWalkout;
    private Double dailyBalanceWalkoutDistance;
    private Double dailyBalanceWalkoutTime;
    private Integer dailyIntakeWalkout;
    private Double dailyIntakeWalkoutDistance;
    private Double dailyIntakeWalkoutTime;
}
