package service;

import java.time.LocalDateTime;

import dto.request.LoginRequest;
import dto.request.RefreshRequest;
import dto.response.AuthResponse;
import entity.AppUser;
import entity.EmailVerificationToken;
import entity.RefreshToken;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import repository.AppUserRepository;
import repository.EmailVerificationTokenRepository;
import repository.RefreshTokenRepository;
import security.TokenUtil;

@ApplicationScoped
public class AuthService {

    @Inject
    AppUserRepository repository;
    @Inject
    RefreshTokenRepository refreshTokenRepository;
    @Inject
    TokenUtil tokenUtil;
    @Inject
    EmailVerificationTokenRepository emailTokenRepository;

    @Transactional
    public AuthResponse login(LoginRequest request, String userAgent, String ipAddress) {

        AppUser user = repository.find("email", request.email)
                .firstResultOptional()
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (user.isDeleted()) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!BcryptUtil.matches(request.password, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!user.isEmailVerified()) {
            throw new UnauthorizedException("Email not verified");
        }

        String accessToken = tokenUtil.generateAccessToken(user);

        String refreshToken = tokenUtil.generateRefreshToken();
        String hashedToken = tokenUtil.hash(refreshToken);

        RefreshToken entity = new RefreshToken();
        entity.setTokenHash(hashedToken);
        entity.setUser(user);
        entity.setDeviceId(request.deviceId);
        entity.setUserAgent(userAgent);
        entity.setIpAddress(ipAddress);
        entity.setExpiresAt(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.update(
                "revoked = true where user.id = ?1 and deviceId = ?2 and revoked = false",
                user.getId(), request.deviceId);

        refreshTokenRepository.persist(entity);

        user.setLastLogin(LocalDateTime.now());
        return new AuthResponse(accessToken, refreshToken, 900, 7 * 24 * 60 * 60, // 7 hari dalam detik
                "Bearer");
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request, String userAgent, String ipAddress) {

        String hashed = tokenUtil.hash(request.refreshToken);

        RefreshToken storedToken = refreshTokenRepository
                .findByTokenHash(hashed)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (!storedToken.getDeviceId().equals(request.deviceId)) {
            throw new UnauthorizedException("Device mismatch");
        }

        // 🚨 Reuse detection
        if (storedToken.isRevoked()) {
            refreshTokenRepository.revokeAllByUser(storedToken.getUser().getId());
            throw new UnauthorizedException("Refresh token reuse detected");
        }

        // Expired check
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }

        AppUser user = storedToken.getUser();

        // Revoke old token (rotation)
        storedToken.setRevoked(true);

        // Generate new access token
        String newAccessToken = tokenUtil.generateAccessToken(user);


        // Generate new refresh token
        String newRefreshToken = tokenUtil.generateRefreshToken();
        String newHashed = tokenUtil.hash(newRefreshToken);

        RefreshToken newEntity = new RefreshToken();
        newEntity.setTokenHash(newHashed);
        newEntity.setUser(user);
        newEntity.setDeviceId(request.deviceId);
        newEntity.setUserAgent(userAgent);
        newEntity.setIpAddress(ipAddress);
        newEntity.setExpiresAt(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.persist(newEntity);

        return new AuthResponse(newAccessToken, newRefreshToken, 900, 7 * 24 * 60 * 60, // 7 hari dalam detik
                "Bearer");
    }

    @Transactional
    public void logout(String refreshToken) {
        String hashed = tokenUtil.hash(refreshToken);

        RefreshToken token = refreshTokenRepository
                .findValidToken(hashed)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        token.setRevoked(true);
    }

    @Transactional
    public void verifyEmail(String rawToken) {

        String hashed = tokenUtil.hash(rawToken);

        EmailVerificationToken token = emailTokenRepository
                .findValidToken(hashed)
                .orElseThrow(() -> new BadRequestException("Invalid or expired token"));

        AppUser user = token.getUser();

        user.setEmailVerified(true);
        token.setUsed(true);
    }

}
