package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.auth.AuthRequest;
import com.uai.BuildLand.models.dto.AuthResponseDto;
import com.uai.BuildLand.models.dto.AuthUserInfoDto;
import com.uai.BuildLand.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequest authRequest) {
        AuthUserInfoDto authUserInfo = authService.authenticate(authRequest);

        if (authUserInfo == null) {
            return ResponseEntity.status(405).build();
        }

        ResponseCookie cookie = ResponseCookie.from("accessToken", authUserInfo.getAuthResponse().getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(9000)
                .sameSite(SameSiteCookies.STRICT.toString())
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authUserInfo.getAuthResponse().getRefreshToken())
                .httpOnly(false)
                .path("/")
                .secure(false)
                .maxAge(172800)
                .sameSite(SameSiteCookies.STRICT.toString())
                .build();

        AuthResponseDto responseBody = new AuthResponseDto(
            authUserInfo.getAuthResponse().getUsername(),
            authUserInfo.getGrupos(),
            authUserInfo.getPermisos()
        );

    return ResponseEntity.status(200)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(responseBody);
    }

    @RequestMapping("/secured")
    public String secured() {
        return "secured";
    }
}
