package Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "dog_daily_intake")
public class PetDailyIntake {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "daily_calorie")
    private Double dailyCalorie;

    @Column(nullable = false, name = "daily_protien")
    private Double dailyProtein;

    @Column(nullable = false, name="daily_fat")
    private Double dailyFat;

    @Column(nullable = false, name = "intake_date")
    private LocalDate intakeDate;

    @Column(nullable = false, name = "daily_walkout")
    private Integer dailyWalkout;

    @OneToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;


}
