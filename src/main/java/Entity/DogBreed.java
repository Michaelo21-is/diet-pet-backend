package Entity;

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

    @Column(nullable = false, name = "DogBreed")
    private String dogBreed;

    @Column(nullable = false, name = "dog_size")
    private String dogSize;

}
