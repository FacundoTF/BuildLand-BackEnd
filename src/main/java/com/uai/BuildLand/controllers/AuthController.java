package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.access.ConjuntoGrupoPermiso;
import com.uai.BuildLand.models.access.Permiso;
import com.uai.BuildLand.models.auth.AuthRequest;
import com.uai.BuildLand.models.dto.AuthUserInfoDto;
import com.uai.BuildLand.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<List<List<String>>> login(@RequestBody AuthRequest authRequest) {
        var authResponse = authService.authenticate(authRequest);
        if (authResponse != null) {
            ResponseCookie cookie = ResponseCookie.from("cookieBuildLand", authResponse.getAuthResponse().getToken())
                    .httpOnly(true)
//                    .secure(true)
                    .path("/")
                    .maxAge(9000)
                    .sameSite(SameSiteCookies.STRICT.toString())
                    .build();

            List<List<String>> conjuntoGrupoPermisos = new ArrayList<>();

            conjuntoGrupoPermisos.add(authResponse.getPermisos());
            conjuntoGrupoPermisos.add(authResponse.getGrupos());

            return ResponseEntity.status(200)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(conjuntoGrupoPermisos);
        } else {
            return ResponseEntity.status(405).build();
        }
    }

    @RequestMapping("/secured")
    public String secured() {
        return "secured";
    }
}
