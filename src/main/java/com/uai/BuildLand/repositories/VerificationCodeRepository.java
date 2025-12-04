package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {

    /**
     * Busca un código de verificación por email
     */
    Optional<VerificationCode> findByEmail(String email);

    /**
     * Busca un código de verificación por email y código
     */
    Optional<VerificationCode> findByEmailAndCode(String email, String code);

    /**
     * Elimina códigos expirados
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationCode v WHERE v.expiresAt < :now")
    void deleteExpiredCodes(LocalDateTime now);

    /**
     * Elimina el código de verificación de un email específico
     */
    @Modifying
    @Transactional
    void deleteByEmail(String email);
}

