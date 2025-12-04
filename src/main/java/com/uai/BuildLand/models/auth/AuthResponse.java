package com.uai.BuildLand.models.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private Long tokenExpiresAt;
    private String refreshToken;
    private Long refreshTokenExpiresAt;
}
