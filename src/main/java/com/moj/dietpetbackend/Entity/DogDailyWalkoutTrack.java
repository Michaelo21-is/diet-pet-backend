package com.moj.dietpetbackend.Entity;

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
// sum up the dog activity
public class DogDailyWalkoutTrack {
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @Column(name = "distance_walked")
    private Double DistanceWalked;

    @Column(name = "walkout_time")
    private Integer WalkoutTime;

    @Column(name = "walkout_duration")
    private Double WalkoutDuration;

    @Column(name = "intake_date")
    private Instant intakeDate;

    @Column(name = "calorie_burned")
    private Double calorieBurned;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;
}
