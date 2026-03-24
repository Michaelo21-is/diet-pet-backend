// user table is designed to be simple for the user to register this is why i apporch the minimalist approch

package Entity;

import Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(unique = true, nullable = false, name = "email")
    private String email;

    @Column(nullable = false, name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    private Role role;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "time_zone", nullable = false)
    private String timeZone;

    @Column(nullable = false, name = "date_of_creation")
    private LocalDateTime dateOfCreation;




}
