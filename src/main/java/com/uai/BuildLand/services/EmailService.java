package com.uai.BuildLand.services;

import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.presupuesto.Presupuesto;
import com.uai.BuildLand.repositories.DistribuidoraRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationCodeService verificationCodeService;
    private final DistribuidoraRepository distribuidoraRepository;

    public EmailService(final JavaMailSender mailSender, final VerificationCodeService verificationCodeService, final DistribuidoraRepository distribuidoraRepository) {
        this.distribuidoraRepository = distribuidoraRepository;
        this.mailSender = mailSender;
        this.verificationCodeService = verificationCodeService;
    }

    public boolean sendVerificationEmail(String to, String subject) {
        try {
            // Generar código de verificación
            String verificationCode = verificationCodeService.createVerificationCode(to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("facufarneda@gmail.com"); // Campo 'from' es requerido
            message.setTo(to);
            message.setSubject(subject);
            message.setText("Su código de verificación es: " + verificationCode +
                          "\n\nEste código expirará en 5 minutos.");
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el email", e);
        }
    }

    public void sendPresupuestoMail(Presupuesto presupuesto) {
        try {
            // Construir el texto con los detalles del presupuesto
            StringBuilder textoBuilder = new StringBuilder();
            for (var detallePresupuesto : presupuesto.getDetalles()) {
                textoBuilder.append("\n").append(detallePresupuesto.getProductoMarca().getProducto().getNombre())
                        .append(": ").append((int) detallePresupuesto.getCantidad()).append(" unidades.");
            }
            String texto = textoBuilder.toString();

            // Tengo que obtener el email de todas las distribuidoras con la categoria del presupuesto
            List<Distribuidora> destinatarios = distribuidoraRepository.findByCategoriasNombre(presupuesto.getCategoria().getNombre());
            for (Distribuidora distribuidora : destinatarios) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("facufarneda@gmail.com");
                message.setTo(distribuidora.getUsuario().getEmail());
                message.setSubject("Nuevo Presupuesto");
                message.setText("Usted ha recibido un nuevo presupuesto con los siguientes detalles:" + texto);
                mailSender.send(message);
            }

        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el email", e);
        }
    }
}
