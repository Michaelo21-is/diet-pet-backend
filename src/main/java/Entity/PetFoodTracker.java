package Entity;


import Enums.FoodSafetyLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "pet_food_tracker")
public class PetFoodTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name="food_score")
    private Integer foodScore;

    @Column(nullable = false, name="food_safety_level")
    @Enumerated(EnumType.STRING)
    private FoodSafetyLevel foodSafetyLevel;

    @Column(nullable = false, name="calories")
    private Integer calories;

    @Column(nullable = false, name="grams")
    private Double grams;

    @Column(nullable = false, name="protein")
    private Double protein;

    @Column(nullable = false, name="ai_review", length = 500)
    private String aiReview;

    @Column(nullable = false, name="food_name")
    private String foodName;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

}
