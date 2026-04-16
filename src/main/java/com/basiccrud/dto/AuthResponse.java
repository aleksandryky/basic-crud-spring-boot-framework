package com.basiccrud.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds) {

    public static AuthResponse of(String accessToken, long expiresInSeconds) {
        return new AuthResponse(accessToken, "Bearer", expiresInSeconds);
    }
}
