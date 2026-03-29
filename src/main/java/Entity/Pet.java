package Entity;

import Enums.PetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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

    @Column(nullable = false, name = "pet_name")
    private String petName;

    @Column(nullable = false, name = "pet_breed")
    private String petBreed;

    @Column(nullable = false, name = "pet_weight_kg")
    private Double petWeightKg;

    @Column(nullable = false, name = "neutered")
    private Boolean neutered;

    @Column(nullable = false, name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = false, name = "calorie_balance")
    private Double calorieBalance;

    @Column(nullable = false, name = "protien_balance")
    private Double proteinBalance;

    @Column(nullable = false, name = "fat_balance")
    private Double fatBalance;

    @Column(nullable = false, name = "pet_walkout_balance")
    private Integer petWalkoutBalance;

    @OneToOne(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Image image;

    @OneToOne(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private PetDailyIntake petDailyIntake;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetWalkOut> petWalkOut;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetFoodTracker> petFoodTracker;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

}
