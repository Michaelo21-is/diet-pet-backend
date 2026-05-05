package com.moj.dietpetbackend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "document_dog_daily_activity")
public class DocumentDogDailyActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "distance_walked_km")
    private Double distanceWalkedKm;
    @Column(name = "duration_minutes")
    private Double durationMinutes;
    @Column(name = "calories_burned")
    private Double caloriesBurned;
    @Column(name = "ai_review")
    private String aiReview;
    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;
}
