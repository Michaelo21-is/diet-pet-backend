package com.moj.dietpetbackend.Entity;

import com.moj.dietpetbackend.Enums.ActivityLevels;
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
// to doucment the dog walkouts
public class DocumentDogDailyActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "distance_walked_km", nullable = false)
    private Double distanceWalkedKm;
    @Column(name = "duration_minutes", nullable = false)
    private Double durationMinutes;
    @Column(name = "calories_burned", nullable = false)
    private Double caloriesBurned;
    @Column(name = "ai_review", nullable = false)
    private String aiReview;
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false)
    private ActivityLevels activityLevel;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;
}
