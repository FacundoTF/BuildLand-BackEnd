package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.auth.AuthRequest;
import com.uai.BuildLand.models.auth.AuthResponse;
import com.uai.BuildLand.models.dto.AuthResponseConjuntoGrupoPermisoDto;
import com.uai.BuildLand.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        var a = authService.authenticate(authRequest);
        if (a != null) {
            // Create secure cookie
            ResponseCookie cookie = ResponseCookie.from("cookieBuildLand", a.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(9000)
                    .sameSite(SameSiteCookies.STRICT.toString())
                    .build();

            return ResponseEntity.status(200).body(a);
        } else {
            return ResponseEntity.status(405).build();
        }
    }
    @RequestMapping("/secured")
    public String secured() {
        return "secured";
    }
}
