package com.hungng.redditbackend.service;

import com.hungng.redditbackend.exception.RedditException;
import com.hungng.redditbackend.model.RefreshToken;
import com.hungng.redditbackend.repository.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;

    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .createdDate(Instant.now())
                .build();

        return refreshTokenRepo.save(refreshToken);
    }

    public void validateRefreshToken(String token) {
        refreshTokenRepo.findByToken(token)
                .orElseThrow(()->new RedditException("Invalid refresh token"));
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepo.deleteByToken(token);
    }

}
