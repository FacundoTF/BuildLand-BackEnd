package com.uai.BuildLand.services;

import com.uai.BuildLand.models.VerificationCode;
import com.uai.BuildLand.repositories.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 5;
    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final VerificationCodeRepository verificationCodeRepository;
    private final SecureRandom random = new SecureRandom();

    /**
     * Genera un código alfanumérico aleatorio sin caracteres especiales
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHANUMERIC_CHARS.length());
            code.append(ALPHANUMERIC_CHARS.charAt(index));
        }
        return code.toString();
    }

    /**
     * Crea y almacena un código de verificación para un email en la base de datos
     * @param email Email del usuario
     * @return El código generado
     */
    @Transactional
    public String createVerificationCode(String email) {
        // Limpiar códigos expirados
        cleanExpiredCodes();

        // Si ya existe un código para este email, eliminarlo
        verificationCodeRepository.deleteByEmail(email);

        // Generar nuevo código
        String code = generateCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        // Guardar en la base de datos
        VerificationCode verificationCode = new VerificationCode(email, code, expirationTime);
        verificationCodeRepository.save(verificationCode);

        return code;
    }

    /**
     * Verifica si un código es válido para un email
     * @param email Email del usuario
     * @param code Código a verificar
     * @return true si el código es válido y no ha expirado, false en caso contrario
     */
    @Transactional
    public boolean verifyCode(String email, String code) {
        Optional<VerificationCode> optionalVerificationCode =
            verificationCodeRepository.findByEmailAndCode(email, code);

        if (optionalVerificationCode.isEmpty()) {
            return false;
        }

        VerificationCode verificationCode = optionalVerificationCode.get();

        // Verificar si el código ha expirado
        if (verificationCode.isExpired()) {
            verificationCodeRepository.deleteByEmail(email);
            return false;
        }

        // Si el código es válido, eliminarlo para que no pueda ser reutilizado
        verificationCodeRepository.deleteByEmail(email);
        return true;
    }

    /**
     * Invalida un código de verificación
     * @param email Email del usuario
     */
    @Transactional
    public void invalidateCode(String email) {
        verificationCodeRepository.deleteByEmail(email);
    }

    /**
     * Limpia los códigos expirados de la base de datos
     */
    @Transactional
    public void cleanExpiredCodes() {
        verificationCodeRepository.deleteExpiredCodes(LocalDateTime.now());
    }
}

