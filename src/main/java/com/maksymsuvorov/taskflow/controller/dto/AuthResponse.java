package com.maksymsuvorov.taskflow.controller.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInMs,
        UserResponse user
) {
    public static AuthResponse bearer(String accessToken, String refreshToken, long expiresInMs, UserResponse user) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInMs, user);
    }
}
