package com.uai.BuildLand.services;

import com.uai.BuildLand.models.dto.AuthUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public String generateToken(Authentication authentication, AuthUserInfoDto authUserInfoDto) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("grupos", authUserInfoDto.getGrupos())
                .claim("permisos", authUserInfoDto.getPermisos())
                .build();
        var encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.encoder.encode(encoderParameters).getTokenValue();
    }

    public String generateRefreshToken(Authentication authentication, AuthUserInfoDto authUserInfoDto) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(48, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("grupos", authUserInfoDto.getGrupos())
                .claim("permisos", authUserInfoDto.getPermisos())
                .build();
        var encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.encoder.encode(encoderParameters).getTokenValue();
    }

    public Long extractExpirationTime(String token) {
        Jwt jwt = decoder.decode(token);
        var exp = (Instant) jwt.getClaim("exp");
        return exp.toEpochMilli();
    }

    public List<String> extractPermissions(String token) {
        Jwt jwt = decoder.decode(token);
        return (List<String>) jwt.getClaim("permisos");
    }

    public String extractUsername(String token) {
        Jwt jwt = decoder.decode(token);
        return jwt.getSubject();
    }

    public List<String> extractGroups(String token) {
        Jwt jwt = decoder.decode(token);
        return (List<String>) jwt.getClaim("grupos");
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Jwt jwt = decoder.decode(token);
        String username = jwt.getSubject();
        assert jwt.getExpiresAt() != null;
        boolean expired = jwt.getExpiresAt().isBefore(Instant.now());

        return  userDetails.getUsername().equals(username) && !expired;
    }
}