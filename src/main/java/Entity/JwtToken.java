package Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jwt_token")
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "access_token")
    private String accessToken;

    @Column(nullable = false, name = "expiration_date_access_token")
    private Instant expirationDateAccessToken;

    @Column(nullable = false, name = "refresh_token")
    private String refreshToken;

    @Column(nullable = false, name = "expiration_date_refresh_token")
    private Instant expirationDateRefreshToken;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
