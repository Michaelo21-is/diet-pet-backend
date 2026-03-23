package Entity;

import Enums.PetType;
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
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "pet_type")
    @Enumerated(EnumType.STRING)
    private PetType petType;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "type_breed")
    private String breed;

    @Column(nullable = false, name = "weight")
    private Double weight;

    @Column(nullable = false, name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = false, name = "calorie_balance")
    private Double calorieBalance;

    @Column(nullable = false, name = "protien_balance")
    private Double proteinBalance;

    @Column(nullable = false, name = "fat_balance")
    private Double fatBalance;

    @Column(nullable = false, name = "carbohydrates_balance")
    private Double carbohydratesBalance;

    @Column(nullable = false, name = "fiber_balance")
    private Double fiberBalance;
}
