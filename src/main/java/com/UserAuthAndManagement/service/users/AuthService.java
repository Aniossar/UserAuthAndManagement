package com.UserAuthAndManagement.service.users;

import com.UserAuthAndManagement.configuration.jwt.JwtProvider;
import com.UserAuthAndManagement.entity.users.PasswordResetToken;
import com.UserAuthAndManagement.entity.users.UserEntity;
import com.UserAuthAndManagement.exception.BadAuthException;
import com.UserAuthAndManagement.payload.request.users.AuthentificationRequest;
import com.UserAuthAndManagement.payload.response.AuthentificationResponse;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Log
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProvider jwtProvider;

    private final Map<String, ArrayList<String>> refreshStorage = new HashMap<>();
    private Map<String, PasswordResetToken> restoringPasswordTokensStorage = new HashMap<>();
    private final int numberOfConcurrentSessions = 5;

    public AuthentificationResponse authenticate(AuthentificationRequest authRequest) {
        try {
            UserEntity userEntity = userService.findByLoginAndPassword(authRequest.getLogin(), authRequest.getPassword());
            if (userEntity.isEnabled()) {
                String accessToken = jwtProvider.generateAccessToken(userEntity.getLogin(), userEntity.getRoleEntity());
                String refreshToken = jwtProvider.generateRefreshToken(userEntity.getLogin());
                ArrayList<String> refreshTokens = refreshStorage.get(userEntity.getLogin());

                if (refreshTokens != null) {
                    if (refreshTokens.size() < numberOfConcurrentSessions) {
                        refreshTokens.add(refreshToken);
                    } else {
                        refreshTokens = new ArrayList<>();
                        refreshTokens.add(refreshToken);
                    }
                } else {
                    refreshTokens = new ArrayList<>();
                    refreshTokens.add(refreshToken);
                }
                refreshStorage.put(userEntity.getLogin(), refreshTokens);
                return new AuthentificationResponse(accessToken, refreshToken);
            }
        } catch (NullPointerException nullPointerException) {
            log.warning("Failed auth with login: " + authRequest.getLogin());
            throw new BadAuthException("Login/password are incorrect");
        }
        return new AuthentificationResponse(null, null);
    }

    public AuthentificationResponse getAccessToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            String login = jwtProvider.getLoginFromRefreshToken(refreshToken);
            ArrayList<String> savedRefreshTokens = refreshStorage.get(login);
            if (savedRefreshTokens != null && savedRefreshTokens.contains(refreshToken)) {
                UserEntity userEntity = userService.findByLogin(login);
                if (userEntity.isEnabled()) {
                    String accessToken = jwtProvider.generateAccessToken(userEntity.getLogin(), userEntity.getRoleEntity());
                    return new AuthentificationResponse(accessToken, null);
                }
            }
        }
        return new AuthentificationResponse(null, null);
    }

    public AuthentificationResponse getNewRefreshToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            String login = jwtProvider.getLoginFromRefreshToken(refreshToken);
            ArrayList<String> savedRefreshTokens = refreshStorage.get(login);
            if (savedRefreshTokens != null && savedRefreshTokens.contains(refreshToken)) {
                UserEntity userEntity = userService.findByLogin(login);
                String accessToken = jwtProvider.generateAccessToken(userEntity.getLogin(), userEntity.getRoleEntity());
                String newRefreshToken = jwtProvider.generateRefreshToken(userEntity.getLogin());
                ArrayList<String> newSavedRefreshTokens = savedRefreshTokens;
                newSavedRefreshTokens.remove(savedRefreshTokens.indexOf(refreshToken));
                newSavedRefreshTokens.add(newRefreshToken);
                refreshStorage.put(userEntity.getLogin(), newSavedRefreshTokens);
                return new AuthentificationResponse(accessToken, newRefreshToken);
            }
        }
        throw new BadAuthException("Token is not valid");
    }

    public String generateRestoringPasswordToken(String login) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        Instant expiryTime = Instant.now().plus(30, ChronoUnit.MINUTES);
        resetToken.setExpiryTime(expiryTime);
        resetToken.setLogin(login);
        restoringPasswordTokensStorage.put(token, resetToken);
        return token;
    }

    public String getLoginFromRestoreToken(@NonNull String token) {
        PasswordResetToken resetToken = restoringPasswordTokensStorage.get(token);
        if (resetToken != null) {
            Instant timeNow = Instant.now();
            if (timeNow.isBefore(resetToken.getExpiryTime())) {
                String userLogin = resetToken.getLogin();
                restoringPasswordTokensStorage.remove(token);
                return userLogin;
            }
        }
        return null;
    }
}
