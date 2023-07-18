package com.hungng.redditbackend.service;

import com.hungng.redditbackend.dto.*;
import com.hungng.redditbackend.exception.RedditException;
import com.hungng.redditbackend.model.User;
import com.hungng.redditbackend.model.VerificationToken;
import com.hungng.redditbackend.repository.UserRepo;
import com.hungng.redditbackend.repository.VerificationTokenRepo;
import com.hungng.redditbackend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepo verificationTokenRepo;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public void signup(RegisterRequest registerRequest) {
        if (userRepo.existsByUsername(registerRequest.getUsername())) {
            throw new RedditException("Username: '" + registerRequest.getUsername() + "' already existed");
        }
        User newUser = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .created(Instant.now())
                .enabled(true)
                .build();
        userRepo.save(newUser);

        String token = generateVerificationToken(newUser);
        mailService.sendMail(new NotificationEmail(
                "Please Activate your account",
                newUser.getEmail(),
                "Thank you for signing up, please click on the below url to activate your account: " +
                        "http://localhost:8080/api/auth/accountVerification/" + token));
    }


    public void verifyAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepo.findByToken(token)
                .orElseThrow(() -> new RedditException("Invalid Token"));
        fetchUserAndEnable(verificationToken);
    }


    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expireAt(Instant.now().minusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUsername(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expireAt(Instant.now().minusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RedditException("User not found with name: " + username));
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RedditException("User not found with name: " + username));
        user.setEnabled(true);
        userRepo.save(user);
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .build();
        verificationTokenRepo.save(verificationToken);
        return token;
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
}
