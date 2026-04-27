package com.moj.dietpetbackend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dog_breed")
public class DogBreed {
    @Id
    private Long id;

    @Column(nullable = false, name = "dog_breed")
    private String dogBreed;

}
