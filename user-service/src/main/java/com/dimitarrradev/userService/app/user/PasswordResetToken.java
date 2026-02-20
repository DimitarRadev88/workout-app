package com.dimitarrradev.userService.app.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="password_reset_tokens")
@Getter
@Setter
@AllArgsConstructor
public class PasswordResetToken {

    private static final int EXPIRATION = 120;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    public PasswordResetToken() {
        this(null, null, null,  LocalDateTime.now().plusMinutes(EXPIRATION));
    }

    public PasswordResetToken(Long id, String token, User user) {
        this(id, token, user, LocalDateTime.now().plusMinutes(EXPIRATION));
    }

}
