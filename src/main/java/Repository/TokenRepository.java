package Repository;

import Entity.JwtToken;
import Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<JwtToken> findByUser_Id(Long userId);
    Optional<JwtToken> findByAccessToken(String accessToken);
    Optional<JwtToken> findByRefreshToken(String refreshToken);
    void deleteAllByUser_Id(Long userId);

}