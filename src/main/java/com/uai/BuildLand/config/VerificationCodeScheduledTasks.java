package com.uai.BuildLand.config;

import com.uai.BuildLand.services.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuración para tareas programadas del sistema de códigos de verificación
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class VerificationCodeScheduledTasks {

    private final VerificationCodeService verificationCodeService;

    /**
     * Tarea programada que se ejecuta cada hora para limpiar códigos expirados
     * Cron: "0 0 * * * *" = cada hora en punto
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanExpiredVerificationCodes() {
        verificationCodeService.cleanExpiredCodes();
        System.out.println("Limpieza automática de códigos de verificación expirados ejecutada");
    }
}

