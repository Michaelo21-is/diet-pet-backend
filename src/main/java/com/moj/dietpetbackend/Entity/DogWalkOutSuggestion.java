package com.moj.dietpetbackend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "dog_walk_out_suggestion")
public class DogWalkOutSuggestion {
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @Column(nullable = false, name = "recommended_daily_distance_km")
    private Double recommendedDailyDistanceKm;

    @Column(nullable = false, name = "recommended_walkout_distance_km")
    private Integer recommendedWalkoutTime;

    @Column(nullable = false, name = "recommended_walkout_time_to_take")
    private Double recommendedWalkoutTimeToTake;

    @Column(nullable = false, name = "ai_review")
    private String aiReview;

    @OneToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;
}
