package Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "dog_daily_walkout")
public class DogDailyWalkoutTrack {
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE)
    @Id
    private Long id;

    @Column(name = "walkout_date")
    private Double DistanceWalked;

    @Column(name = "walkout_time")
    private Integer WalkoutTime;

    @Column(name = "walkout_time_to_take")
    private Double WalkoutTimeToTake;

    @Column(name = "intake_date")
    private Instant intakeDate;

    @OneToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;
}
