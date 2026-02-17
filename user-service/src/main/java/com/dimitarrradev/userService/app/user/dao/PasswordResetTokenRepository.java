package com.dimitarrradev.userService.app.user.dao;

import com.dimitarrradev.userService.app.user.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndUser_emailAndExpirationDateIsAfter(String token, String email, LocalDateTime expiration);

}
