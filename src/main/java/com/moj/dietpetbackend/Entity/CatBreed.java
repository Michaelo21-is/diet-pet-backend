package com.moj.dietpetbackend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cat_breed")
public class CatBreed {
    @Id
    private Integer id;

    @Column(nullable = false, name = "cat_breed")
    private String catBreed;


}
