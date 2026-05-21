package com.moj.dietpetbackend.Entity;

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
@Table(name = "pet_daily_intake")
// is for tracking the balance of the pet diet
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

    @Column(name = "daily_balance_calories")
    private Double dailyBalanceCalories;

    @Column(name = "daily_protein_balance")
    private Double dailyProteinBalance;

    @Column(name = "daily_fat_balance")
    private Double dailyFatBalance;




    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;


}
