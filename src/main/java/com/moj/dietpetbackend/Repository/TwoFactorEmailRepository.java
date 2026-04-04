package com.moj.dietpetbackend.Repository;

import com.moj.dietpetbackend.Entity.TwoFactorEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TwoFactorEmailRepository  extends JpaRepository<TwoFactorEmail, Long> {
    Optional<TwoFactorEmail> findByUserId(Long userId);
}
