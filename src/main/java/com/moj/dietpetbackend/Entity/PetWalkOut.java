package com.moj.dietpetbackend.Entity;

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
@Table(name = "pet_walk_out")
public class PetWalkOut {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "distance_km")
    private Double distanceKm;

    @Column(nullable = false, name = "calories_burned")
    private Double caloriesBurned;

    @Column(nullable = false, name = "duration_minutes")
    private Integer durationMinutes;

    @Column(nullable = false, name = "walk_out_time")
    private Instant walkOutTime;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;
}
