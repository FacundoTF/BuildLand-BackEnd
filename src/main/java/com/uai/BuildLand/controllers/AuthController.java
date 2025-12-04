package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.auth.AuthRequest;
import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.models.dto.AuthResponseDto;
import com.uai.BuildLand.models.dto.AuthUserInfoDto;
import com.uai.BuildLand.models.dto.VerificationCodeDto;
import com.uai.BuildLand.repositories.UsuarioRepository;
import com.uai.BuildLand.services.AuthService;
import com.uai.BuildLand.services.EmailService;
import com.uai.BuildLand.services.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/token")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequest authRequest) {
        AuthUserInfoDto authUserInfo = authService.authenticate(authRequest);

        if (authUserInfo == null) {
            return ResponseEntity.status(405).build();
        }

        ResponseCookie cookie = ResponseCookie.from("accessToken", authUserInfo.getAuthResponse().getToken()).httpOnly(true).secure(false).path("/").maxAge(9000).sameSite(SameSiteCookies.STRICT.toString()).build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authUserInfo.getAuthResponse().getRefreshToken()).httpOnly(false).path("/").secure(false).maxAge(172800).sameSite(SameSiteCookies.STRICT.toString()).build();

        AuthResponseDto responseBody = new AuthResponseDto(authUserInfo.getAuthResponse().getUsername(), authUserInfo.getGrupos(), authUserInfo.getPermisos());

        return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE, cookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(responseBody);
    }

    @PostMapping("/restorePassword")
    public ResponseEntity<Void> createRestorationCode(@RequestBody Usuario email) {
        try {
            boolean emailSent = emailService.sendVerificationEmail(email.getEmail(), "Recuperar contraseña");
            if (!emailSent) {
                return ResponseEntity.status(500).build();
            } else {
                return ResponseEntity.status(200).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/verifyPasswordCode")
    public ResponseEntity<Void> verifyRestorationCode(@RequestBody VerificationCodeDto verificationData) {
        try {
            // Verificar que los datos no sean nulos
            if (verificationData == null ||
                verificationData.getEmail() == null ||
                verificationData.getCode() == null) {
                return ResponseEntity.status(400).build(); // Bad Request
            }

            // Verificar el código
            boolean isValid = verificationCodeService.verifyCode(
                verificationData.getEmail(),
                verificationData.getCode()
            );

            if (isValid) {
                return ResponseEntity.status(200).build(); // Código válido
            } else {
                return ResponseEntity.status(401).build(); // Código inválido o expirado
            }
        } catch (Exception e) {
            System.err.println("Error al verificar código: " + e.getMessage());
            return ResponseEntity.status(500).build(); // Internal Server Error
        }
    }

    @RequestMapping("/changePassword")
    public ResponseEntity<Void> restorePassword(@RequestBody Usuario usuario) {
        try {
            Optional<Usuario> usuarioModificado = usuarioRepository.findByEmail(usuario.getEmail());
            if (usuarioModificado.isPresent()) {
                Usuario user = usuarioModificado.get();
                user.setPassword(usuario.getPassword());
                usuarioRepository.save(user);
            } else {
                return ResponseEntity.status(404).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.status(200).build();
    }
}
