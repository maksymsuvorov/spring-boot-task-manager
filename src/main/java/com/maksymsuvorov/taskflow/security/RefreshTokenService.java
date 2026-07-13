package com.maksymsuvorov.taskflow.security;

import com.maksymsuvorov.taskflow.model.RefreshToken;
import com.maksymsuvorov.taskflow.model.User;
import com.maksymsuvorov.taskflow.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public String issue(User user) {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(sha256Hex(rawToken));
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(this.refreshExpirationMs / 1000));

        this.refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public User consume(String rawToken) {
        RefreshToken refreshToken = this.refreshTokenRepository.findByTokenHashWithUser(sha256Hex(rawToken))
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token."));

        User user = refreshToken.getUser();
        this.refreshTokenRepository.delete(refreshToken);

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token has expired.");
        }

        return user;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredTokens() {
        int deleted = this.refreshTokenRepository.deleteAllExpiredBefore(LocalDateTime.now());

        if (deleted > 0) {
            log.info("Deleted {} expired refresh tokens.", deleted);
        }
    }

    private static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }

}
